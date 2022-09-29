package edu.zut.epidemic.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import edu.zut.epidemic.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * Created by Intellij IDEA
 * Author: yi cheng
 * Date: 2022/9/29
 * Time: 20:47
 **/
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${files.upload.path}")
    public String uploadPath;

    @PostMapping("/upload")
    public Result upload(@RequestParam MultipartFile file) throws IOException {
        /*使用uuid生成随机文件名，避免重名覆盖问题*/
        String originalFilename = file.getOriginalFilename();//获取上传文件名，可以用字符串截取lastIndexOf，推荐hutool
        String fileType = FileUtil.extName(originalFilename);// jpg\png\jpeg...(不带点.)
        long fileSize = file.getSize();// 获取文件大小
        // 生成随机uuid作为文件名
        String uuid = IdUtil.fastSimpleUUID();// uuid也可以使用 UUID.randomUUID().toString();
        // 加上文件类型的完整文件名
        String fileUUIDName = uuid + StrUtil.DOT + fileType;// StrUtil.DOT 就是一个点（.）
        log.info("文件存储路径：{}，存储文件名：{}，文件大小约：{}", uploadPath, fileUUIDName, fileSize / 1024 + "K");
        // 如果配置文件指定的目录不存在则创建
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            // 不存在就创建
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(uploadPath + fileUUIDName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Result.success(fileUUIDName);
    }
}
