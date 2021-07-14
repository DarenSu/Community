package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    // 提前准备好logger，时刻备用着
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    // 替换符    敏感词的替换     修改好修改
    private static final String REPLACEMENT = "***";

    // 根节点
    private TrieNode rootNode = new TrieNode();
    //   该注解表示该方法为初始化方法，当容器实例化bean之后，调用SensitiveFilter这个构造器之后，这个方法被自动调用，
    //   bean在服务启动的时候初始化，也就是服务启动之后这个累就开始初始化
    //   在构造方法执行后，自动调用，一般用于类的初始化
    @PostConstruct
    public void init() {
        try (
                // 这是一个字节楼，什么时候关闭，在final关闭，所以写一个try() catch,在try里面开启的话，会自动生成final，比较方便
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));// 字节流转字符流，然后字符流转成缓冲流，效率高一些
        ) {
            String keyword; // 每次读取的词存在keyword里面
            while ((keyword = reader.readLine()) != null) {
                // 添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败: " + e.getMessage());
        }
    }

    // 将一个敏感词添加到前缀树中     方法是内部使用不需要共有
    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c); // 寻找是否已有该一级子节点

            if (subNode == null) {
                // 初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }

            // 指向子节点,进入下一轮循环
            tempNode = subNode;

            // 打标记， 设置结束标识
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     *
     * @param text 待过滤的文本
     * @return 过滤后的文本
     *
     * 参数是原来的字符串，返回的是过滤后的字符串
     *
     *  方法思路：需要三个指针，指针 1指向根节点， 指针2指向文本中字符串的前面节点，  指针三指向文本中的字符串的后面节点，作为滑动
     *      然后首先是将敏感词中的字符过滤掉，尤其是敏感词之间的字符，
     *      然后进行敏感词替换，也就是检查敏感词
     *            1、不是敏感词，直接计入过滤后的文本中
     *            2、找到了最后一位，也就是敏感词结束的标记，是敏感词，将指针2和指针3之间的为敏感词，替换为****
     *            3、剩下的是检查到了敏感词，但是没有检查到结束，所以继续检查
     *
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) { // 判空
            return null;
        }

        // 指针1  默认指向根
        TrieNode tempNode = rootNode;
        // 指针2  指向字符串，并进行定位
        int begin = 0;
        // 指针3  指向字符串，在2找到一级词的时候，3进行遍历寻找完整的敏感词
        int position = 0;
        // 结果  存储最终结果
        StringBuilder sb = new StringBuilder();
        // 当指针三到达结尾的时候，停止遍历
        while (position < text.length()) {
            char c = text.charAt(position); // 得到当前的某一个字符

            // 跳过符号    因为现在一些敏感词制作这也聪明了，很多都会加几个表情符号或者空格来规避
            if (isSymbol(c)) {
                // 若指针1处于根节点,将此符号计入结果,让指针2向下走一步，当然指针1也会走
                if (tempNode == rootNode) {
                    sb.append(c);
                    begin++;//指针2走
                }
                // 无论符号在开头或中间,指针3都向下走一步
                position++;
                continue;
            }

            // 检查下级节点
            tempNode = tempNode.getSubNode(c);// 当前节点变成下级节点
            if (tempNode == null) {// 下级没有节点，不是敏感词   因为之前没有找到敏感词
                // 以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                // 进入下一个位置
                position = ++begin; // 将当前节点加如文本之后，第二个指针自增，并赋值给第三个指针
                // 重新指向根节点
                tempNode = rootNode;// 第一个节点重新指向根节点
            } else if (tempNode.isKeywordEnd()) {  //  当前节点是敏感节点，并且已经找到了敏感词的最后一位
                // 发现敏感词,将begin~position字符串替换掉
                sb.append(REPLACEMENT);  // 替换敏感词
                // 进入下一个位置
                begin = ++position;
                // 重新指向根节点
                tempNode = rootNode;
            } else {   // 检查到一半的敏感词，还没有检查完，那么第三个指针继续检查
                // 检查下一个字符
                position++;
            }
        }

        // 将最后一批字符计入结果
        sb.append(text.substring(begin));

        return sb.toString();
    }

    // 判断是否为符号     判断正常符号：CharUtils.isAsciiAlphanumeric(c)
    //                  ！CharUtils.isAsciiAlphanumeric(c)
    private boolean isSymbol(Character c) {
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    // 前缀树
    private class TrieNode {

        // 关键词结束标识
        private boolean isKeywordEnd = false;

        // 子节点 (key是下级字符, value是下级节点)  子节点默认为空
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        // 添加子节点 的方法，得需要是公有的
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        // 获取子节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }

    }

}
