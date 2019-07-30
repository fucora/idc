package com.iwellmass.idc.app.vo;

import com.iwellmass.idc.app.vo.graph.GraphVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobVO {

    @ApiModelProperty("详细node实例")
    private List<NodeJobVO> nodeJobVOS;

    @ApiModelProperty("图形依赖关系")
    private GraphVO graphVO;
}
