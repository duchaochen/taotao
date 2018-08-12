package com.taotao.controller;

import com.taotao.common.utils.JsonUtils;
import com.taotao.utils.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class FileUpload {

    @Value("${IMAGE_SERVER_URL}")
    private String IMAGE_SERVER_URL;

    /**
     * 为了兼容多个浏览器需要直接返回json字符串
     * @param uploadFile
     * @return
     * @throws Exception
     */
    @RequestMapping("/pic/upload")
    @ResponseBody
    public String imageUpload(MultipartFile uploadFile) throws Exception {
        String originalFilename = uploadFile.getOriginalFilename();
        String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        FastDFSClient fastDFSClient = new FastDFSClient("classpath:resource/client.conf");
        String url = fastDFSClient.uploadFile(uploadFile.getBytes(), extName);
        url = IMAGE_SERVER_URL + url;
        Map map = new HashMap();
        try {
            map.put("error", 0);
            map.put("url", url);

        } catch (Exception e) {
            map = new HashMap();
            map.put("error", 1);
            map.put("message", "错误信息");
        }
        return JsonUtils.objectToJson(map);
    }
}
