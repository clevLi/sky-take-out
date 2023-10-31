package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口（上传文件）")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;


    /**
     * 文件上传到OSS，返回文件路径
     * @param file
     * @return
     */
    @ApiOperation("上传文件")
    @PostMapping("/upload")
    public Result uploadFile(MultipartFile file){

        log.info("文件上传：{}",file);
        try {
            String filename = file.getOriginalFilename();
            String suffix = filename.substring(filename.lastIndexOf("."));
            String name = UUID.randomUUID().toString()  + suffix;
            String filepath = aliOssUtil.upload(file.getBytes(), name);
            return Result.success(filepath);
        } catch (IOException e) {
            e.printStackTrace();
            log.error(MessageConstant.UPLOAD_FAILED);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
