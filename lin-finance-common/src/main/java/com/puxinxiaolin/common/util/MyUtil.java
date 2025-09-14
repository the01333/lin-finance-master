package com.puxinxiaolin.common.util;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MyUtil {

    /**
     * 随机生成num位数字
     *
     * @param length 数字的长度
     * @return
     */
    public static int getRandom(int length) {
        return (int) ((Math.random() * 9 + 1) * Math.pow(10, length - 1));
    }

    /**
     * 获取下载 excel 输出流信息设置
     *
     * @param response
     * @param fileName
     * @return
     * @throws UnsupportedEncodingException
     */
    public static HttpServletResponse getDownloadExcelResponse(HttpServletResponse response, String fileName) throws UnsupportedEncodingException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        
        // 这里 URLEncoder.encode 可以防止中文乱码 当然和 easyexcel 没有关系
        fileName = URLEncoder.encode(fileName, "UTF-8")
                .replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        return response;
    }

}