package com.hqw.websocket.ws;

import lombok.Data;
import org.nutz.dao.entity.Record;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 采集命令vo
 *
 * @version <pre>
 * Author	Version		Date		Changes
 * Administrator 	1.0  2020年05月13日 Created
 *
 * </pre>
 * @since 1.
 */
@Data
public class GatherControlVo implements Serializable {
    private static final long serialVersionUID = 2326226224299693L;

    /***/
    private Integer id;
    /**
     * 采集的数据业务日期
     */
    private String gatherDate;
    /**
     * 医疗机构代码
     */
    private String orgId;
    /**
     * 数据类型
     */
    private String dataType;
    /**
     * 每次任务的执行业务UUID
     */
    private String transId;
    /**
     * 0:默认状态，由job生成;1:其它路径如控制台生成的命令
     */
    private Integer controlType;
    /**
     * 请求参数，json格式
     */
    private String params;
    /**
     * 0:未有效的任务，此状态前置机无须同步;1:有效的任务
     */
    private Integer enableState;
    /**
     * 请求同步标识;0：初始化（新的命令）;1：已经同步
     */
    private Integer syncState;
    /**
     * 0:采集未启动
     * 1:正在采集
     * 2:采集异常
     * 3:取消
     * 9:采集成功
     */
    private Integer gatherState;
    /**
     * 上传状态
     * 0:未准备
     * 1:正在准备
     * 2:准备就绪
     * 3:正在上传
     * 9:上传完成
     */
    private Integer uploadState;
    /**
     * 入库状态：
     * 0：未入库
     * 1：正在入库
     * 9：入库完成
     */
    private Integer saveState;
    /**
     * 该批次上传总数
     */
    private Integer dataTotal;
    /**
     * 已经上传的数量
     */
    private Integer dataUpload;
    /**
     * 已经入库的数量
     */
    private Integer dataSave;
    /**
     * 重试次数,上限3次
     */
    private Integer retryTimes;
    /**
     * 错误信息
     */
    private String errMsg;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * 上传的数据
     */
    private List<Record> data;

}