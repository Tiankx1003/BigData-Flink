package com.tian.flink.day01.wc

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}

/**
 * 流处理wordcount
 *
 * @author tian
 * @date 2019/10/18 10:07
 * @version 1.0.0
 */
object StreamWordCount {
    def main(args: Array[String]): Unit = {
        // 从外部命令中获取参数
        val params: ParameterTool = ParameterTool.fromArgs(args)
        val host: String = params.get("host")
        val port: Int = params.getInt("port")
        // 创建流处理环境
        val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
        //env.setParallelism(2) //设置全局并行度
        // 接收socket文本流
        val textDstream: DataStream[String] = env.socketTextStream(host, port)

        // flatMap和Map需要引用的隐式转换
        import org.apache.flink.api.scala._
        val dataStream: DataStream[(String, Int)] = textDstream
            .flatMap(_.split("\\s"))
            .filter(_.nonEmpty)
            .map((_, 1))
            .keyBy(0)
            .sum(1)

        dataStream.print().setParallelism(2) //并行度设置

        // 启动executor，执行任务
        env.execute("Socket stream word count")
    }

}
