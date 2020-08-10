package com.talkman.saas.utils;

/**
 * @author wny
 * @date 2019/6/3 10:02
 */

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import com.talkman.saas.common.exception.ResultCode;
import com.talkman.saas.common.exception.ResultException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * @Author doger.wang
 * 对象存储api封装
 */
@Slf4j
public class OssUtil {


    /**
     * 域名
     */
    public final static String OSS_END_POINT_OUT = "oss-cn-shanghai.aliyuncs.com";
    public final static String OSS_END_POINT = "oss-cn-shanghai-internal.aliyuncs.com";

    /**
     * 账号
     */
    private final static String OSS_ACCESS_KEY_ID;

    /**
     * 密匙
     */
    private final static String OSS_ACCESS_KEY_SECRET;

    /**
     * 存储空间 测试
     */
    private final static String OSS_BUCKET_NAME;

    static {
        ResourceBundle bundle = ResourceBundle.getBundle("oss");
        String oss_access_key_id = bundle.getString("OSS_ACCESS_KEY_ID");
        String oss_access_key_secret = bundle.getString("OSS_ACCESS_KEY_SECRET");
        String oss_bucket_name = bundle.getString("OSS_BUCKET_NAME");
        OSS_ACCESS_KEY_ID = oss_access_key_id;
        OSS_ACCESS_KEY_SECRET = oss_access_key_secret;
        OSS_BUCKET_NAME = oss_bucket_name;

    }


    /**
     * URL有效期
     */
    private final static Date OSS_URL_EXPIRATION = DateUtils.addDays(new Date(), 365 * 10);


    private volatile static OSSClient instance = null;

    private OssUtil() {
    }

    /**
     * Oss 实例化
     *
     * @return
     */
    private static OSSClient getOssClient() {
        if (instance == null) {
            synchronized (OssUtil.class) {
                if (instance == null) {
                    instance = new OSSClient(OSS_END_POINT, OSS_ACCESS_KEY_ID, OSS_ACCESS_KEY_SECRET);
                }
            }
        }
        return instance;
    }



    /*    *//**
     * 文件路径的枚举
     *//*
    public enum FileDirType {

        IMAGES("images"), VIDEO("video"), CAROUSEL("carousel"), MENU("menu");

        private String dir;

        FileDirType(String dir) {
            this.dir = dir;
        }

        @JsonValue
        public String getDir() {
            return dir;
        }
    }*/


    /**
     * 当Bucket 不存在时候创建Bucket
     */
    private static void createBuchet() {
        try {
            if (!OssUtil.getOssClient().doesBucketExist(OSS_BUCKET_NAME)) {
                OssUtil.getOssClient().createBucket(OSS_BUCKET_NAME);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ClientException("创建Bucket失败,请核对Bucket名称(规则：只能包含小写字母、数字和短横线，必须以小写字母和数字开头和结尾，长度在3-63之间)");
        }
    }


    /**
     * 文件上传的文件后缀
     *
     * @param FilenameExtension
     * @return
     */
    private static String getContentType(String FilenameExtension) {
        if (FilenameExtension.equalsIgnoreCase("jpeg") ||
                FilenameExtension.equalsIgnoreCase("jpg") ||
                FilenameExtension.equalsIgnoreCase("png")) {
            return "image/jpeg";
        }
        return "multipart/form-data";
    }


    /**
     * 上传OSS服务器 如果同名文件会覆盖服务器上的
     *
     * @param
     * @param
     * @return
     */
    private static String uploadFile(MultipartFile file, String dir) {
        String fileName = String.format("%s%s", UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5), file.getOriginalFilename());
        try (InputStream inputStream = file.getInputStream()) {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(inputStream.available());
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setHeader("Pragma", "no-cache");
            objectMetadata.setContentType(FilenameUtils.getExtension("." + file.getOriginalFilename()));
            objectMetadata.setContentDisposition("inline;filename=" + fileName);
            PutObjectResult putObjectResult = OssUtil.getOssClient().putObject(OSS_BUCKET_NAME, dir + "/" + fileName, inputStream, objectMetadata);
            System.out.println(putObjectResult);
            return fileName;
        } catch (OSSException oe) {
            System.out.println("Error Message: " + oe.getErrorCode());
            System.out.println("Error Code:    " + oe.getErrorCode());
            System.out.println("Request ID:    " + oe.getRequestId());
            System.out.println("Host ID:       " + oe.getHostId());
            return null;
        } catch (ClientException ce) {
            System.out.println("Error Message: " + ce.getMessage());
            return null;
        } catch (IOException e) {
            System.out.println("Error Message: " + e.getMessage());
            return null;
        }
    }

    /**
     * 分片上传OSS服务器 如果同名文件会覆盖服务器上的
     *
     * @param fileDirType
     * @param file
     * @param session
     * @return
     */
    private static String uploadFileByParts(MultipartFile file, String dir, HttpSession session) {
        String originalFilename = file.getOriginalFilename().replaceAll(" ", "");
        //session.setAttribute("fileName", file.getOriginalFilename().replaceAll(" ", ""));
        String fileName = String.format("%s%s", UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5), file.getOriginalFilename());
        try {

/*            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(inputStream.available());
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setHeader("Pragma", "no-cache");
            objectMetadata.setContentType(FilenameUtils.getExtension("." + file.getOriginalFilename()));
            objectMetadata.setContentDisposition("inline;filename=" + fileName);*/
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(OSS_BUCKET_NAME, dir + "/" + fileName);
            //request.setObjectMetadata(objectMetadata);
            OSSClient ossClient = OssUtil.getOssClient();
            // 初始化分片。
            InitiateMultipartUploadResult upresult = ossClient.initiateMultipartUpload(request);
            // 返回uploadId，它是分片上传事件的唯一标识，您可以根据这个ID来发起相关的操作，如取消分片上传、查询分片上传等。
            String uploadId = upresult.getUploadId();
            List<PartETag> partETags = new ArrayList<PartETag>();
            // 计算文件有多少个分片。
            final long partSize = 2 * 1024 * 1024L;   // 5MB
            //final File sampleFile = new File(file.getOriginalFilename());
            long fileLength = file.getSize();
            int partCount = (int) (fileLength / partSize);
            if (fileLength % partSize != 0) {
                partCount++;
            }
            log.info("oss分片上传开始，总片数==>" + partCount);
            ExecutorService executorService = Executors.newFixedThreadPool(8);
            // 遍历分片上传。
            for (int i = 0; i < partCount; i++) {
                InputStream in = file.getInputStream();
                long startPos = i * partSize;
                long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
                //InputStream instream = file.getInputStream();
                // 跳过已经上传的分片。
                in.skip(startPos);
                UploadPartRequest uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(OSS_BUCKET_NAME);
                uploadPartRequest.setKey(dir + "/" + fileName);
                uploadPartRequest.setUploadId(uploadId);
                uploadPartRequest.setInputStream(in);
                // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100KB。
                uploadPartRequest.setPartSize(curPartSize);
                // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出这个范围，OSS将返回InvalidArgument的错误码。
                uploadPartRequest.setPartNumber(i + 1);
                // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
                executorService.execute(new partUploader(originalFilename, ossClient, uploadPartRequest, partETags, partCount, session));
                // 每次上传分片之后，OSS的返回结果会包含一个PartETag。PartETag将被保存到partETags中。

            }

            //等待所有的分片完成
            executorService.shutdown();
            while (!executorService.isTerminated()) {
                try {
                    executorService.awaitTermination(2, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }

            //验证是否所有的分片都完成
            if (partETags.size() != partCount) {
                throw new ResultException(ResultCode.PARAMER_EXCEPTION, "文件的某些部分上传失败！");
            }
            /**3.完成分片上传**/
            //排序。partETags必须按分片号升序排列
            Collections.sort(partETags, new Comparator<PartETag>() {
                @Override
                public int compare(PartETag o1, PartETag o2) {
                    return o1.getPartNumber() - o2.getPartNumber();
                }
            });
            // 创建CompleteMultipartUploadRequest对象。
            // 在执行完成分片上传操作时，需要提供所有有效的partETags。OSS收到提交的partETags后，会逐一验证每个分片的有效性。当所有的数据分片验证通过后，OSS将把这些分片组合成一个完整的文件。
            CompleteMultipartUploadRequest completeMultipartUploadRequest =
                    new CompleteMultipartUploadRequest(OSS_BUCKET_NAME, dir + "/" + fileName, uploadId, partETags);

            // 如果需要在完成文件上传的同时设置文件访问权限，请参考以下示例代码。
            // completeMultipartUploadRequest.setObjectACL(CannedAccessControlList.PublicRead);

            // 完成上传。
            CompleteMultipartUploadResult completeMultipartUploadResult = ossClient.completeMultipartUpload(completeMultipartUploadRequest);

            // 关闭OSSClient。
            //ossClient.shutdown();
            return fileName;
        } catch (OSSException oe) {
            System.out.println("Error Message: " + oe.getErrorCode());
            System.out.println("Error Code:    " + oe.getErrorCode());
            System.out.println("Request ID:    " + oe.getRequestId());
            System.out.println("Host ID:       " + oe.getHostId());
            return null;
        } catch (ClientException ce) {
            System.out.println("Error Message: " + ce.getMessage());
            return null;
        } catch (IOException e) {
            System.out.println("Error Message: " + e.getMessage());
            return null;
        }
    }


    private static String uploadFileByBytes(String OriginalFileName, String dir, ByteArrayInputStream stream) {
        String fileName = String.format("%s%s", UUID.randomUUID().toString().replaceAll("-", "").substring(0, 15), OriginalFileName);
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(stream.available());
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setHeader("Pragma", "no-cache");
            objectMetadata.setContentType(FilenameUtils.getExtension("." + OriginalFileName));
            objectMetadata.setContentDisposition("inline;filename=" + fileName);
            PutObjectResult putObjectResult = OssUtil.getOssClient().putObject(OSS_BUCKET_NAME, dir + "/" + fileName, stream, objectMetadata);
            System.out.println(putObjectResult);
            return fileName;
        } catch (OSSException oe) {
            System.out.println("Error Message: " + oe.getErrorCode());
            System.out.println("Error Code:    " + oe.getErrorCode());
            System.out.println("Request ID:    " + oe.getRequestId());
            System.out.println("Host ID:       " + oe.getHostId());
            return null;
        } catch (ClientException ce) {
            System.out.println("Error Message: " + ce.getMessage());
            return null;
        }
    }


    private static String uploadFileByBytesOnlyOne(String OriginalFileName, String dir, ByteArrayInputStream stream) {
        String fileName =OriginalFileName;
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(stream.available());
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setHeader("Pragma", "no-cache");
            objectMetadata.setContentType(FilenameUtils.getExtension("." + OriginalFileName));
            objectMetadata.setContentDisposition("inline;filename=" + fileName);
            PutObjectResult putObjectResult = OssUtil.getOssClient().putObject(OSS_BUCKET_NAME, dir + "/" + fileName, stream, objectMetadata);
            System.out.println(putObjectResult);
            return fileName;
        } catch (OSSException oe) {
            System.out.println("Error Message: " + oe.getErrorCode());
            System.out.println("Error Code:    " + oe.getErrorCode());
            System.out.println("Request ID:    " + oe.getRequestId());
            System.out.println("Host ID:       " + oe.getHostId());
            return null;
        } catch (ClientException ce) {
            System.out.println("Error Message: " + ce.getMessage());
            return null;
        }
    }

    /**
     * 获取文件路径
     *
     * @param fileUrl
     * @param
     * @return
     */
    private static String getFileUrl(String fileUrl, String dir) {
        if (StringUtils.isEmpty(fileUrl)) {
            throw new RuntimeException("文件地址为空!");
        }
        String[] split = fileUrl.split("/");

        URL url = OssUtil.getOssClient().generatePresignedUrl(OSS_BUCKET_NAME, dir + "/" + split[split.length - 1], OSS_URL_EXPIRATION);
        if (url == null) {
            throw new RuntimeException("获取OSS文件URL失败!");
        }
        String replace1 = url.toString().replace("http", "https");
        String replace2 = replace1.replace(OSS_END_POINT, OSS_END_POINT_OUT);
        return replace2;
    }


    /**
     * 文件上传
     *
     * @param file
     * @param
     * @return
     */
    public static OssInfo upload(MultipartFile file, String dir) {
        OssUtil.createBuchet();
        String fileName = OssUtil.uploadFile(file, dir);
        String fileOssUrl = OssUtil.getFileUrl(fileName, dir);
        int firstChar = fileOssUrl.indexOf("?");
        if (firstChar > 0) {
            fileOssUrl = fileOssUrl.substring(0, firstChar);
        }
        OssInfo ossInfo = new OssInfo();
        ossInfo.setFileName(fileName);
        ossInfo.setFileOssUrl(fileOssUrl);
        return ossInfo;
    }

    public static OssInfo uploadByParts(MultipartFile file, String dir, HttpSession session) {
        OssUtil.createBuchet();
        String fileName = OssUtil.uploadFileByParts(file, dir, session);
        String fileOssUrl = OssUtil.getFileUrl(fileName, dir);
        int firstChar = fileOssUrl.indexOf("?");
        if (firstChar > 0) {
            fileOssUrl = fileOssUrl.substring(0, firstChar);
        }
        OssInfo ossInfo = new OssInfo();
        ossInfo.setFileName(fileName);
        ossInfo.setFileOssUrl(fileOssUrl);
        return ossInfo;
    }

    /**
     * 文件上传
     *
     * @param ByteArrayInputStream
     * @param
     * @return
     */
    public static OssInfo upload(String OriginalFileName, String dir, ByteArrayInputStream stream ,boolean isOnly) {
        OssUtil.createBuchet();
        String fileName;
        if (isOnly){
            fileName = OssUtil.uploadFileByBytesOnlyOne(OriginalFileName, dir, stream);
        }else {
            fileName = OssUtil.uploadFileByBytes(OriginalFileName, dir, stream);
        }
        String fileOssUrl = OssUtil.getFileUrl(fileName, dir);
        int firstChar = fileOssUrl.indexOf("?");
        if (firstChar > 0) {
            fileOssUrl = fileOssUrl.substring(0, firstChar);
        }
        OssInfo ossInfo = new OssInfo();
        ossInfo.setFileName(fileName);
        ossInfo.setFileOssUrl(fileOssUrl);
        return ossInfo;
    }


    /**
     * 获取路径地址
     *
     * @param fileName
     * @return
     */
    public static String getPathUrl(String fileName) {
        return fileName.substring(fileName.indexOf(OSS_END_POINT) + OSS_END_POINT.length() + 1);
    }


    /**
     * 文件删除
     *
     * @param keys
     */
    public static void delete(List<String> keys) {
        List<String> newKeys = keys.stream().map((item) -> {
            return OssUtil.getPathUrl(item);
        }).collect(Collectors.toList());
        try {
            DeleteObjectsResult deleteObjectsResult = OssUtil.getOssClient().deleteObjects(new DeleteObjectsRequest(OSS_BUCKET_NAME).withKeys(newKeys));
            List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();
            System.out.println(deletedObjects);
        } catch (OSSException oe) {
            System.out.println("Error Message: " + oe.getErrorCode());
            System.out.println("Error Code:    " + oe.getErrorCode());
            System.out.println("Request ID:    " + oe.getRequestId());
            System.out.println("Host ID:       " + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message: " + ce.getMessage());
        }

    }


    /**
     * 批量下载为zip
     *
     * @param req
     * @param response
     * @param keyList  oss文件路径集合
     * @param fileName 下载出来的zip文件名（一般以合同名命名） 如：测试合同,zip
     */
    public static void downForZip(HttpServletRequest req, HttpServletResponse response,
                                  List<String> keyList, String fileName) {
        //String header1 = req.getHeader(CommonConstant.Authentication);
        //req.removeAttribute(CommonConstant.Authentication);
        // 创建临时文件
        File zipFile = null;
        try {
            zipFile = File.createTempFile(fileName, ".zip");
            FileOutputStream f = new FileOutputStream(zipFile);
            /**
             * 作用是为任何OutputStream产生校验和
             * 第一个参数是制定产生校验和的输出流，第二个参数是指定Checksum的类型 （Adler32（较快）和CRC32两种）
             */
            CheckedOutputStream csum = new CheckedOutputStream(f, new Adler32());
            // 用于将数据压缩成Zip文件格式
            ZipOutputStream zos = new ZipOutputStream(csum);
            OSSClient ossClient = getOssClient();
            for (String ossFile : keyList) {
                String name = getPathUrl(ossFile);
                String decode = URLDecoder.decode(name, "utf-8");
                // 获取Object，返回结果为OSSObject对象
                OSSObject ossObject = ossClient.getObject(new GetObjectRequest(OSS_BUCKET_NAME, decode));
                // 读去Object内容 返回
                InputStream inputStream = ossObject.getObjectContent();
                // 对于每一个要被存放到压缩包的文件，都必须调用ZipOutputStream对象的putNextEntry()方法，确保压缩包里面文件不同名
                //String name = ossFile.substring(ossFile.lastIndexOf("/") + 1);
                zos.putNextEntry(new ZipEntry(decode));
                int bytesRead = 0;
                // 向压缩文件中输出数据
                while ((bytesRead = inputStream.read()) != -1) {
                    zos.write(bytesRead);
                }
                inputStream.close();
                zos.closeEntry(); // 当前文件写完，定位为写入下一条项目
            }
            zos.close();
            fileName = fileName + ".zip";
            String header = req.getHeader("User-Agent").toUpperCase();
/*            if (header.contains("MSIE") || header.contains("TRIDENT") || header.contains("EDGE")) {
                fileName = URLEncoder.encode(fileName, "utf-8");
                //IE下载文件名空格变+号问题
                fileName = fileName.replace("+", "%20");
            } else {
                fileName = new String(fileName.getBytes(), "ISO8859-1");
            }*/
            response.reset();
            response.setContentType("application/json");
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setHeader("Location", fileName);
            response.setHeader("Cache-Control", "max-age=0");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            FileInputStream fis = new FileInputStream(zipFile);
            BufferedInputStream buff = new BufferedInputStream(fis);
            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
            byte[] car = new byte[1024];
            int l = 0;
            while (l < zipFile.length()) {
                int j = buff.read(car, 0, 1024);
                l += j;
                out.write(car, 0, j);
            }
            // 关闭流
            fis.close();
            buff.close();
            out.close();
            ossClient.shutdown();
            // 删除临时文件
            zipFile.delete();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OssInfo uploadSpotZip(String name, File file, String type, String dir, HttpSession session) throws IOException {
        OssUtil.createBuchet();
        //byte[] bytes = FileUtils.readFileToByteArray(file);
        String fileName = OssUtil.uploadFileByBytesWithOne(name, dir, file, session);
        String fileOssUrl = OssUtil.getFileUrl(fileName, dir);
        int firstChar = fileOssUrl.indexOf("?");
        if (firstChar > 0) {
            fileOssUrl = fileOssUrl.substring(0, firstChar);
        }
        OssInfo ossInfo = new OssInfo();
        ossInfo.setFileName(fileName);
        ossInfo.setFileOssUrl(fileOssUrl);
        return ossInfo;


    }

    private static String uploadFileByBytesWithOne(String OriginalFileName, String dir, File file, HttpSession session) {
        String fileName = OriginalFileName;
        try {

/*            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(inputStream.available());
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setHeader("Pragma", "no-cache");
            objectMetadata.setContentType(FilenameUtils.getExtension("." + file.getOriginalFilename()));
            objectMetadata.setContentDisposition("inline;filename=" + fileName);*/
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(OSS_BUCKET_NAME, dir + "/" + fileName);
            //request.setObjectMetadata(objectMetadata);
            OSSClient ossClient = OssUtil.getOssClient();
            // 初始化分片。
            InitiateMultipartUploadResult upresult = ossClient.initiateMultipartUpload(request);
            // 返回uploadId，它是分片上传事件的唯一标识，您可以根据这个ID来发起相关的操作，如取消分片上传、查询分片上传等。
            String uploadId = upresult.getUploadId();
            List<PartETag> partETags = new ArrayList<PartETag>();
            // 计算文件有多少个分片。
            final long partSize = 5 * 1024 * 1024L;   // 2MB
            //final File sampleFile = new File(file.getOriginalFilename());
            long fileLength = file.length();
            int partCount = (int) (fileLength / partSize);
            if (fileLength % partSize != 0) {
                partCount++;
            }
            log.info("oss分片上传开始，总片数==>" + partCount + "x5Mb");
            ExecutorService executorService = Executors.newFixedThreadPool(8);
            // 遍历分片上传。
            for (int i = 0; i < partCount; i++) {
                InputStream in = new FileInputStream(file);
                long startPos = i * partSize;
                long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
                //InputStream instream = file.getInputStream();
                // 跳过已经上传的分片。
                in.skip(startPos);
                UploadPartRequest uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(OSS_BUCKET_NAME);
                uploadPartRequest.setKey(dir + "/" + fileName);
                uploadPartRequest.setUploadId(uploadId);
                uploadPartRequest.setInputStream(in);
                // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100KB。
                uploadPartRequest.setPartSize(curPartSize);
                // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出这个范围，OSS将返回InvalidArgument的错误码。
                uploadPartRequest.setPartNumber(i + 1);
                // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
                executorService.execute(new partUploader(null, ossClient, uploadPartRequest, partETags, partCount, session));
                // 每次上传分片之后，OSS的返回结果会包含一个PartETag。PartETag将被保存到partETags中。

            }

            //等待所有的分片完成
            executorService.shutdown();
            while (!executorService.isTerminated()) {
                try {
                    executorService.awaitTermination(2, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }

            //验证是否所有的分片都完成
            if (partETags.size() != partCount) {
                throw new ResultException(ResultCode.PARAMER_EXCEPTION, "文件的某些部分上传失败！");
            }
            /**3.完成分片上传**/
            //排序。partETags必须按分片号升序排列
            Collections.sort(partETags, new Comparator<PartETag>() {
                @Override
                public int compare(PartETag o1, PartETag o2) {
                    return o1.getPartNumber() - o2.getPartNumber();
                }
            });
            // 创建CompleteMultipartUploadRequest对象。
            // 在执行完成分片上传操作时，需要提供所有有效的partETags。OSS收到提交的partETags后，会逐一验证每个分片的有效性。当所有的数据分片验证通过后，OSS将把这些分片组合成一个完整的文件。
            CompleteMultipartUploadRequest completeMultipartUploadRequest =
                    new CompleteMultipartUploadRequest(OSS_BUCKET_NAME, dir + "/" + fileName, uploadId, partETags);

            // 如果需要在完成文件上传的同时设置文件访问权限，请参考以下示例代码。
            // completeMultipartUploadRequest.setObjectACL(CannedAccessControlList.PublicRead);

            // 完成上传。
            CompleteMultipartUploadResult completeMultipartUploadResult = ossClient.completeMultipartUpload(completeMultipartUploadRequest);


            // 关闭OSSClient。
            //ossClient.shutdown();

            return fileName;
        } catch (OSSException oe) {
            System.out.println("Error Message: " + oe.getErrorCode());
            System.out.println("Error Code:    " + oe.getErrorCode());
            System.out.println("Request ID:    " + oe.getRequestId());
            System.out.println("Host ID:       " + oe.getHostId());
            return null;
        } catch (ClientException ce) {
            System.out.println("Error Message: " + ce.getMessage());
            return null;
        } catch (IOException e) {
            System.out.println("Error Message: " + e.getMessage());
            return null;
        }
    }

    public static void deleteDir(String dir) {
        OSSClient ossClient = OssUtil.getOssClient();
        ObjectListing objectListing = ossClient.listObjects(OSS_BUCKET_NAME,dir);
        List<OSSObjectSummary> objectSummaries = objectListing.getObjectSummaries();
        List<String> list = new ArrayList<>();
        for (OSSObjectSummary s : objectSummaries) {
            list.add(s.getKey());
        }
        if (list.size()>0){
            ossClient.deleteObjects(new DeleteObjectsRequest(OSS_BUCKET_NAME).withKeys(list));
            deleteDir(dir);
        }else {
            return;
        }
    }


    public static class OssInfo {
        public String fileName;
        public String fileOssUrl;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileOssUrl() {
            return fileOssUrl;
        }

        public void setFileOssUrl(String fileOssUrl) {
            this.fileOssUrl = fileOssUrl;
        }
    }

    public static void main(String[] args) {
        getPathUrl("11");
    }


    private static class partUploader implements Runnable {
        private String fileName;
        private OSSClient ossClient;
        private UploadPartRequest uploadPartRequest;
        private List<PartETag> partETags;
        private int partCount;
        private HttpSession session;

        public partUploader(String originalFilename, OSSClient ossClient, UploadPartRequest uploadPartRequest, List<PartETag> partETags, int partCount, HttpSession session) {
            this.fileName = originalFilename;
            this.ossClient = ossClient;
            this.uploadPartRequest = uploadPartRequest;
            this.partETags = partETags;
            this.partCount = partCount;
            this.session = session;
        }

        @Override
        public void run() {
            UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
            //每次上传分片之后，OSS的返回结果会包含一个PartETag。PartETag将被保存到PartETags中。
            int precent = (this.partETags.size() + 1) * 100 / partCount;
            log.info(precent + "%==>" + uploadPartRequest.getKey() + "上传进度");
            if (null != session) {
                session.setAttribute("filePercent", precent);
            }
            synchronized (this.partETags) {
                this.partETags.add(uploadPartResult.getPartETag());
            }

        }
    }
}
