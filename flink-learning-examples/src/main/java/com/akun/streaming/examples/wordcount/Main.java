package com.akun.streaming.examples.wordcount;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class Main {

    public static void main(String[] args) throws Exception {
        //1、创建流程序的运行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //2、给流程序的运行环境设置全局的配置（从参数 args 获取）
        env.getConfig().setGlobalJobParameters(ParameterTool.fromArgs(args));
        //3、构建数据源，WORDS 是个字符串数组
        env.fromElements(WORDS)
                //4、将字符串进行分隔然后收集，组装后的数据格式是 (word、1)，1 代表 word 出现的次数为 1
                .flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
                    @Override
                    public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
                        String[] splits = value.toLowerCase().split("\\W+");

                        for (String split : splits) {
                            if (split.length() > 0) {
                                out.collect(new Tuple2<>(split, 1));
                            }
                        }
                    }
                })
                //5、根据 word 关键字进行分组（0 代表对第一个字段分组，也就是对 word 进行分组）
                .keyBy(0)
                //6、对单个 word 进行计数操作
                .reduce(new ReduceFunction<Tuple2<String, Integer>>() {
                    @Override
                    public Tuple2<String, Integer> reduce(Tuple2<String, Integer> value1, Tuple2<String, Integer> value2) throws Exception {
                        return new Tuple2<>(value1.f0, value1.f1 + value1.f1);
                    }
                })
                //7、打印所有的数据流，格式是 (word，count)，count 代表 word 出现的次数
                .print();
        //8、开始执行 Job
        //Streaming 程序必须加这个才能启动程序，否则不会有结果
        env.execute("akun —— word count streaming demo");
    }

    private static final String[] WORDS = new String[]{
            "To be, or not to be,--that is the question:--",
            "Whether 'tis nobler in the mind to suffer"
    };
//    private static final String[] WORDS = new String[]{
//            "To be, or not to be,--that is the question:--",
//            "Whether 'tis nobler in the mind to suffer",
//            "The slings and arrows of outrageous fortune",
//            "Or to take arms against a sea of troubles,",
//            "And by opposing end them?--To die,--to sleep,--",
//            "No more; and by a sleep to say we end",
//            "The heartache, and the thousand natural shocks",
//            "That flesh is heir to,--'tis a consummation",
//            "Devoutly to be wish'd. To die,--to sleep;--",
//            "To sleep! perchance to dream:--ay, there's the rub;",
//            "For in that sleep of death what dreams may come,",
//            "When we have shuffled off this mortal coil,",
//            "Must give us pause: there's the respect",
//            "That makes calamity of so long life;",
//            "For who would bear the whips and scorns of time,",
//            "The oppressor's wrong, the proud man's contumely,",
//            "The pangs of despis'd love, the law's delay,",
//            "The insolence of office, and the spurns",
//            "That patient merit of the unworthy takes,",
//            "When he himself might his quietus make",
//            "With a bare bodkin? who would these fardels bear,",
//            "To grunt and sweat under a weary life,",
//            "But that the dread of something after death,--",
//            "The undiscover'd country, from whose bourn",
//            "No traveller returns,--puzzles the will,",
//            "And makes us rather bear those ills we have",
//            "Than fly to others that we know not of?",
//            "Thus conscience does make cowards of us all;",
//            "And thus the native hue of resolution",
//            "Is sicklied o'er with the pale cast of thought;",
//            "And enterprises of great pith and moment,",
//            "With this regard, their currents turn awry,",
//            "And lose the name of action.--Soft you now!",
//            "The fair Ophelia!--Nymph, in thy orisons",
//            "Be all my sins remember'd."
//    };
}
