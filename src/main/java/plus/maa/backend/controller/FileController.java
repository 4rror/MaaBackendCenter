package plus.maa.backend.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import org.hibernate.validator.constraints.Length;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import plus.maa.backend.common.annotation.AccessLimit;
import plus.maa.backend.config.SpringDocConfig;
import plus.maa.backend.config.security.AuthenticationHelper;
import plus.maa.backend.controller.request.file.ImageDownloadDTO;
import plus.maa.backend.controller.response.MaaResult;
import plus.maa.backend.service.FileService;

/**
 * @author LoMu
 * Date  2023-03-31 16:41
 */

@RestController
@RequestMapping("file")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;
    private final AuthenticationHelper helper;

    /**
     * 支持匿名
     *
     * @param file file
     * @return 上传成功, 数据已被接收
     */
    @AccessLimit
    @GetMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MaaResult<String> uploadFile(@RequestParam(name = "file") MultipartFile file,
                                        @RequestParam String type,
                                        @RequestParam String version,
                                        @RequestParam(required = false) String classification,
                                        @RequestParam(required = false) String label) {
        fileService.uploadFile(file, type, version, classification, label, helper.getUserIdOrIpAddress());
        return MaaResult.success("上传成功,数据已被接收");
    }

    @Operation(summary = "下载文件")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/zip", schema = @Schema(type = "string", format = "binary"))
    )
    @SecurityRequirement(name = SpringDocConfig.SECURITY_SCHEME_NAME)
    @AccessLimit
    @GetMapping("/download")
    public void downloadSpecifiedDateFile(
            @Parameter(description = "日期 yyyy-MM-dd") String date,
            @Parameter(description = "在日期之前或之后[before,after]") String beLocated,
            @Parameter(description = "对查询到的数据进行删除") boolean delete,
            HttpServletResponse response
    ) {
        fileService.downloadDateFile(date, beLocated, delete, response);
    }

    @Operation(summary = "下载文件")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/zip", schema = @Schema(type = "string", format = "binary"))
    )
    @SecurityRequirement(name = SpringDocConfig.SECURITY_SCHEME_NAME)
    @PostMapping("/download")
    public void downloadFile(@RequestBody @Valid
                             ImageDownloadDTO imageDownloadDTO,
                             HttpServletResponse response) {
        fileService.downloadFile(imageDownloadDTO, response);
    }

    @Operation(summary = "关闭uploadfile接口")
    @GetMapping("/disable")
    public MaaResult<String> disable() {
        return MaaResult.success(fileService.disable());
    }

    @Operation(summary = "开启uploadfile接口")
    @GetMapping("/enable")
    public MaaResult<String> enable() {
        return MaaResult.success(fileService.enable());
    }

}
