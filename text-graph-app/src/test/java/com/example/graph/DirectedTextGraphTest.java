package com.example.graph;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;

/**
 * 测试 DirectedTextGraph#queryBridgeWords 方法，使用 buildFromFile() 从 test-graph.txt 构造图。
 *
 * 测试文本内容（放在 src/test/resources/test-graph.txt）：
 *   The scientist carefully analyzed the data, wrote a detailed report, and shared the report
 *   with the team, but the team requested more data, so the scientist analyzed it again.
 *
 * 经过 buildFromFile() 构造的图里（全部小写、非字母替换为空格、连续空格合并）大致会有如下单词顺序：
 *   ["the", "scientist", "carefully", "analyzed", "the", "data", "wrote", "a", "detailed", "report",
 *    "and", "shared", "the", "report", "with", "the", "team", "but", "the", "team", "requested", "more",
 *    "data", "so", "the", "scientist", "analyzed", "it", "again"]
 *
 * 由此产生的部分关键有向边（只列跟测试相关的）：
 *   scientist → carefully
 *   carefully → analyzed
 *   analyzed → the
 *   the → data
 *   ... （省略中间部分，因为 queryBridgeWords 只找 w1→mid →w2 这种两步简短路径）
 *   team → requested
 *   ...
 *   (重复了 "scientist analyzed" 一次，但那是直接 analyzed→the→data 之类，不影响下文)
 *
 * 下面针对这张图，编写以下 6 个测试用例及它们的期望输出：
 *
 * 1. (scientist, analyzed)
 *    └── 期望输出：
 *        The bridge words from "scientist" to "analyzed" are: carefully.
 *
 * 2. (scientist, data)
 *    └── 期望输出：
 *        No bridge words from "scientist" to "data"!
 *
 * 3. (fortitude, analyzed)
 *    └── 期望输出：（“fortitude” 根本不在图里，或者 “analyzed” 在图里，但是只要 w1/w2 任意一个不在，就返回下面）
 *        No "fortitude" or "analyzed" in the graph!
 *
 * 4. (scientist, society)
 *    └── 期望输出：（“society” 不在图里）
 *        No "scientist" or "society" in the graph!
 *
 * 5. (test, temp)
 *    └── 期望输出：（二者都不在图里）
 *        No "test" or "temp" in the graph!
 *
 * 6. (null, null) → 在这里我们用 ("", "") 代替 null,null，这样 queryBridgeWords 会
 *    先把 w1="" 和 w2="" 转为小写（依然是 ""），接着 !containsVertex("")，于是返回：
 *        No "" or "" in the graph!
 */
public class DirectedTextGraphTest {

    private DirectedTextGraph dtg;

    @BeforeEach
    public void setUp() throws Exception {
        dtg = new DirectedTextGraph();

        // 1. 从 classpath 中定位 test-graph.txt
        ClassLoader cl = getClass().getClassLoader();
        URL resUrl = cl.getResource("test-graph.txt");
        if (resUrl == null) {
            fail("无法在 classpath 中找到 test-graph.txt；请确认它在 src/test/resources/ 下");
        }

        // 2. 将 URL 转为 File
        File graphFile = new File(resUrl.toURI());

        // 3. 调用 buildFromFile，让程序自行根据 test-graph.txt 构造有向图
        dtg.buildFromFile(graphFile);
    }

    @Test
    public void testScientistAnalyzed() {
        String actual = dtg.queryBridgeWords("scientist", "analyzed");
        System.out.println("【Scientist→Analyzed 的返回】 " + actual);
        String expected = "The bridge words from \"scientist\" to \"analyzed\" are: carefully.";
        assertEquals(expected, actual);
    }

    @Test
    public void testScientistData_NoBridge() {
        String actual = dtg.queryBridgeWords("scientist", "data");
        System.out.println("【Scientist→Data 的返回】 " + actual);
        String expected = "No bridge words from \"scientist\" to \"data\"!";
        assertEquals(expected, actual);
    }

    @Test
    public void testFortitudeAnalyzed_NoVertex() {
        String actual = dtg.queryBridgeWords("fortitude", "analyzed");
        System.out.println("【Fortitude→Analyzed 的返回】 " + actual);
        String expected = "No \"fortitude\" or \"analyzed\" in the graph!";
        assertEquals(expected, actual);
    }

    @Test
    public void testScientistSociety_NoVertex() {
        String actual = dtg.queryBridgeWords("scientist", "society");
        System.out.println("【Scientist→Society 的返回】 " + actual);
        String expected = "No \"scientist\" or \"society\" in the graph!";
        assertEquals(expected, actual);
    }

    @Test
    public void testTestTemp_NoVertex() {
        String actual = dtg.queryBridgeWords("test", "temp");
        System.out.println("【Test→Temp 的返回】 " + actual);
        String expected = "No \"test\" or \"temp\" in the graph!";
        assertEquals(expected, actual);
    }

    @Test
    public void testEmptyEmpty_NoVertex() {
        // 用空字符串 "" 代替用户“什么也不输入”，queryBridgeWords 会先 toLowerCase("") → "",
        // 然后 !containsVertex("") → 返回 No "" or "" in the graph!
        String actual = dtg.queryBridgeWords("", "");
        System.out.println("【→ 的返回】 " + actual);
        String expected = "No \"\" or \"\" in the graph!";
        assertEquals(expected, actual);
    }
}
