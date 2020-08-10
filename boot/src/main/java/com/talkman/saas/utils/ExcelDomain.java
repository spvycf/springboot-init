package com.talkman.saas.utils;


import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @param <T>
 * @Author AnYi
 * @Date 2018年1月17日 下午2:11:01
 * @Version 2.0
 * 使用示例
 * ExcelDomain<实体类型> domain = new ExcelDomain<>();
 * 列名
 * String[] rowsName = {"服务机构", "机构员工","交易类型","买卖类型","交易金额 (元)","交易时间" };
 * 列名对应的实体字段，依照驼峰
 * String[] rowsValue  = {"cpaName","empName","tradeType","buyType","tradeAmount","tradeDate"};
 * domain.setFileName("关联用户交易量");
 * 查询出来的数据List<实体类型> result
 * domain.setDataset(result);
 * domain.setRowsName(rowsName);
 * domain.setRowsValue(rowsValue);
 * domain.setTitle("关联用户交易量");
 * domain.setOut(response.getOutputStream());
 * domain.setHttpServletRequest(request);
 * domain.setHttpServletResponse(response);
 * domain.exportExcel(domain);
 * <p>
 * 关于字段别名转换请在数据库完成
 * CASE BUY_TYPE
 * WHEN "1" THEN "买单"
 * WHEN "2" THEN "卖单"
 * END as "buyType"
 */
public class ExcelDomain<T> {

    /**
     * @param fileName
     * 下载文件名
     */
    String fileName;
    /**
     * @param title
     * 表格标题名
     */
    String title;
    /**
     * @param rowsName
     * 表格属性列名数组
     */
    String[] rowsName;
    /**
     * @param rowsValue
     * 表格属性列名对应的bean中的字段数组，和rowsName一一对应
     */
    String[] rowsValue;
    /**
     * 需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的
     * javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
     */
    Collection<T> dataset;
    /**
     * *@param out
     * 与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
     */
    OutputStream out;
    /**
     * * @param pattern
     * 如果有时间数据，设定输出格式。默认为"yyyy-MM-dd"
     */
    String pattern;

    HttpServletRequest httpServletRequest;

    HttpServletResponse httpServletResponse;


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getRowsName() {
        return rowsName;
    }


    public void setRowsName(String[] rowsName) {
        this.rowsName = rowsName;
    }


    public String[] getRowsValue() {
        return rowsValue;
    }


    public void setRowsValue(String[] rowsValue) {
        this.rowsValue = rowsValue;
    }


    public Collection<T> getDataset() {
        return dataset;
    }


    public void setDataset(Collection<T> dataset) {
        this.dataset = dataset;
    }


    public OutputStream getOut() {
        return out;
    }

    public void setOut(OutputStream out) {
        this.out = out;
    }


    public String getPattern() {
        return pattern;
    }


    public void setPattern(String pattern) {
        this.pattern = pattern;
    }


    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }

    public void setHttpServletResponse(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }

    public void exportExcel(ExcelDomain<T> domain) {


        String title = domain.getTitle();
        String[] headers = domain.getRowsName();
        String[] fields = domain.getRowsValue();
        Collection<T> dataset = domain.getDataset();
        OutputStream out = domain.getOut();
        HttpServletRequest request = domain.getHttpServletRequest();
        HttpServletResponse response = domain.getHttpServletResponse();
        String pattern = "yyyy-MM-dd";
        if (!"".equals(domain.getPattern()) && null != domain.getPattern()) {
            pattern = domain.getPattern();
        }

        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为15个字节
        //sheet.setDefaultColumnWidth((short) 20);
        sheet.setDefaultColumnWidth(20);
        // 生成一个样式
        HSSFCellStyle style = workbook.createCellStyle();
        //style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setAlignment(HorizontalAlignment.CENTER);
        HSSFFont font = workbook.createFont();
        font.setFontName("微软雅黑");
        style.setFont(font);
         /* // 设置这些样式
         style.setFillForegroundColor(HSSFColor.WHITE.index);
         style.setFillPattern(CellStyle.SOLID_FOREGROUND);
         style.setBorderBottom(CellStyle.BORDER_THIN);
         style.setBorderLeft(CellStyle.BORDER_THIN);
         style.setBorderRight(CellStyle.BORDER_THIN);
         style.setBorderTop(CellStyle.BORDER_THIN);
         style.setAlignment(CellStyle.ALIGN_CENTER);
         // 生成一个字体
         HSSFFont font = workbook.createFont();
         font.setColor(HSSFColor.BLACK.index);
         font.setFontHeightInPoints((short) 12);
         font.setBoldweight(Font.BOLDWEIGHT_BOLD);
         // 把字体应用到当前的样式
         // 生成并设置另一个样式
         HSSFCellStyle style2 = workbook.createCellStyle();
         style2.setFillForegroundColor(HSSFColor.WHITE.index);
         style2.setFillPattern(CellStyle.SOLID_FOREGROUND);
         style2.setBorderBottom(CellStyle.BORDER_THIN);
         style2.setBorderLeft(CellStyle.BORDER_THIN);
         style2.setBorderRight(CellStyle.BORDER_THIN);
         style2.setBorderTop(CellStyle.BORDER_THIN);
         style2.setAlignment(CellStyle.ALIGN_LEFT);
         style2.setVerticalAlignment(CellStyle.VERTICAL_CENTER);


         HSSFCellStyle style3 = workbook.createCellStyle();
         style3.setFillForegroundColor(HSSFColor.WHITE.index);
         style3.setFillPattern(CellStyle.SOLID_FOREGROUND);
         style3.setAlignment(CellStyle.ALIGN_CENTER);
         style3.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

         HSSFFont font3 = workbook.createFont();
         font3.setColor(HSSFColor.BLACK.index);
         font3.setFontHeightInPoints((short) 14);
         font3.setBoldweight(Font.BOLDWEIGHT_BOLD);
         style3.setFont(font3);

         // 生成另一个字体
//       HSSFFont font2 = workbook.createFont();
//       font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
//       // 把字体应用到当前的样式
//       style2.setFont(font2);
*/
        // 声明一个画图的顶级管理器
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
        // 定义注释的大小和位置,详见文档
        // HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
        // 设置注释内容
        //comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
        // 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
        //comment.setAuthor("BLDZ");

        //标题
//        HSSFRow row = sheet.createRow(0);
/*         // 单元格合并
         // 四个参数分别是：起始行，结束行，起始列，结束列
         Integer len = headers.length;
         sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, len-1));
         HSSFCell celltitle =  row.createCell(0);
         //celltitle.setCellStyle(style3);
         celltitle.setCellValue(title);*/


        //产生表格标题行
        HSSFRow row2 = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row2.createCell(i);
            cell.setCellStyle(style);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }


        //遍历集合数据，产生数据行
        Iterator<T> it = dataset.iterator();
        int index = 0;
        while (it.hasNext()) {
            index++;
            HSSFRow row = sheet.createRow(index);
            T t = it.next();
            //利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
            //Field[] fields = t.getClass().getDeclaredFields();、
            for (int i = 0; i < fields.length; i++) {
                HSSFCell cell = row.createCell(i);
                cell.setCellStyle(style);
                //Field field = fields[i];
                //String fieldName = field.getName();
                String fieldName = fields[i];
                String getMethodName = "get"
                        + fieldName.substring(0, 1).toUpperCase()
                        + fieldName.substring(1);
                try {
                    Class<? extends Object> tCls = t.getClass();
                    Method getMethod = tCls.getMethod(getMethodName, new Class[]{});
                    Object value = getMethod.invoke(t, new Object[]{});
                    //判断值的类型后进行强制类型转换
                    String textValue = null;
                    if (value != null) {
                        if (value instanceof Date) {
                            Date date = (Date) value;
                            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                            textValue = sdf.format(date);
                        } else if (value instanceof byte[]) {
                            // 有图片时，设置行高为60px;
                            row.setHeightInPoints(60);
                            // 设置图片所在列宽度为80px,注意这里单位的一个换算
                            sheet.setColumnWidth(i, (short) (35.7 * 80));
                            // sheet.autoSizeColumn(i);
                            byte[] bsValue = (byte[]) value;
                            HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0,
                                    1023, 255, (short) 6, index, (short) 6, index);
                            //anchor.setAnchorType(2);
                            anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
                            patriarch.createPicture(anchor, workbook.addPicture(
                                    bsValue, Workbook.PICTURE_TYPE_JPEG));
                        } else {
                            //其它数据类型都当作字符串简单处理
                            textValue = value.toString();
                        }
                        //如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
                        if (textValue != null) {
                            Pattern p = Pattern.compile("^//d+(//.//d+)?$");
                            Matcher matcher = p.matcher(textValue);
                            if (matcher.matches()) {
                                //是数字当作double处理
                                //                            DecimalFormat df = new DecimalFormat("0");
//                            cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
//
//                            String whatYourWant = df.format(Double.parseDouble(textValue));
//                            cell.setCellValue(whatYourWant);

                                cell.setCellValue(Double.parseDouble(textValue));
                            } else {
                                HSSFRichTextString richString = new HSSFRichTextString(textValue);
//                           HSSFFont font3 = workbook.createFont();
//                           font3.setColor(HSSFColor.BLUE.index);
//                           richString.applyFont(font3);
                                cell.setCellValue(richString);
                            }
                        }
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } finally {
                    //清理资源
                }
            }

        }
        try {
            //设置response响应参数
            String filename = domain.getFileName() + ".xls";
            //获取浏览器类型
            String agent = request.getHeader("User-Agent");
            String mimeType = request.getServletContext().getMimeType(filename);
//            //根据浏览器类型对文件名编码
            filename = encodeDownloadFilename(filename, agent);
//            //response的输出流
            //ServletOutputStream os = response.getOutputStream();
            //设置前台浏览器返回数据的格式xls
            response.setContentType(mimeType);
            //设置前台浏览器数据的打开方式
            response.setHeader("Content-Disposition", "attachment;filename=" + filename);
            response.setCharacterEncoding("utf-8");
            response.flushBuffer();

            workbook.write(out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 下载文件时，针对不同浏览器，进行附件名的编码
     *
     * @param filename 下载文件名
     * @param agent    客户端浏览器
     * @return 编码后的下载附件名
     * @throws IOException
     */
    @SuppressWarnings("restriction")
    public static String encodeDownloadFilename(String fileName, String agent)
            throws IOException {
        //agent="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36";
        if (agent != null && agent.indexOf("MSIE") != -1) {

            fileName = URLEncoder.encode(fileName, "UTF8");

        } else {

            fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");

        }
        return fileName;
    }


}
