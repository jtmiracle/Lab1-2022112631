package com.example.gui;

import com.example.graph.DirectedTextGraph;
import com.example.graph.PageRankCalculator;
import com.example.graph.RandomWalker;
import com.example.utils.FileUtils;
import org.jgrapht.alg.scoring.PageRank;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Map;

public class MainFrame extends JFrame {
    private DirectedTextGraph graph;

    public MainFrame(String[] args) {
        super("Text Graph Analyzer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        File inputFile = null;
        if (args.length >= 1) {
            inputFile = new File(args[0]);
        } else {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select text file");
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                inputFile = chooser.getSelectedFile();
            }
        }
        if (inputFile == null || !inputFile.exists()) {
            JOptionPane.showMessageDialog(this, "No valid file selected. Exiting.");
            System.exit(1);
        }

        // 构建有向图
        graph = new DirectedTextGraph();
        graph.buildFromFile(inputFile);

        // UI 布局
        JPanel panel = new JPanel(new GridLayout(7, 1, 5, 5));
        add(panel, BorderLayout.CENTER);

        panel.add(button("1. 展示并保存有向图", e -> graph.showAndSaveGraph("graph.png")));
        panel.add(button("2. 查询桥接词", e -> queryBridge()));
        panel.add(button("3. 生成新文本 (插入桥接词)", e -> generateNew()));
        // 在 MainFrame.java 的构造函数中，替换原有的“最短路径”按钮那一行：
        /*panel.add(button("4. 最短路径", e -> {
            String w1 = JOptionPane.showInputDialog(this, "起点 word1:");
            if (w1 == null || w1.isBlank()) return;

            String w2 = JOptionPane.showInputDialog(this,
                    "终点 word2 (留空将计算到所有可达节点):");

            if (w2 == null || w2.isBlank()) {
                // 只输入一个单词，调用单参版本
                String result = graph.calcShortestPath(w1);
                JOptionPane.showMessageDialog(
                        this,
                        result,
                        String.format("从 \"%s\" 到其他节点的最短路径", w1),
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                // 同时输入两个单词，调用可视化高亮版 + 显示路径信息
                String result = graph.calcShortestPath(w1, w2);
                JOptionPane.showMessageDialog(
                        this,
                        result,
                        String.format("从 \"%s\" 到 \"%s\" 的最短路径", w1, w2),
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        }));*/
        // 在图形化界面中添加“最短路径”按钮时的逻辑——要求两个参数均非空，否则弹出提示
        panel.add(button("4. 最短路径", e -> {
            // 弹出第一个对话框，输入起点
            String w1 = JOptionPane.showInputDialog(this, "起点 word1:");
            if (w1 == null || w1.isBlank()) {
                // 起点为空时，直接弹出提示，并返回
                JOptionPane.showMessageDialog(
                        this,
                        "请输入起点和终点，均不能为空。",
                        "输入错误",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // 弹出第二个对话框，输入终点
            String w2 = JOptionPane.showInputDialog(this, "终点 word2:");
            if (w2 == null || w2.isBlank()) {
                // 终点为空时，也弹出提示并返回
                JOptionPane.showMessageDialog(
                        this,
                        "请输入起点和终点，均不能为空。",
                        "输入错误",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // 如果走到这里，说明 w1 和 w2 均非空，调用两参版本进行计算
            String result = graph.calcShortestPath(w1, w2);
            JOptionPane.showMessageDialog(
                    this,
                    result,
                    String.format("从 \"%s\" 到 \"%s\" 的最短路径", w1, w2),
                    JOptionPane.INFORMATION_MESSAGE
            );
        }));
        panel.add(button("5. 计算 PageRank", e -> calcPageRank()));
        panel.add(button("6. 随机游走", e -> randomWalk()));
    }

    private JButton button(String text, ActionListener listener) {
        JButton b = new JButton(text);
        b.addActionListener(listener);
        return b;
    }

    private void queryBridge() {
        String w1 = JOptionPane.showInputDialog(this, "word1:");
        String w2 = JOptionPane.showInputDialog(this, "word2:");
        String res = graph.queryBridgeWords(w1, w2);
        JOptionPane.showMessageDialog(this, res);
    }

    private void generateNew() {
        String input = JOptionPane.showInputDialog(this, "输入一段新文本：");
        String out = graph.generateNewText(input);
        JOptionPane.showMessageDialog(this, out);
    }

    /*private void shortestPath() {
        String w1 = JOptionPane.showInputDialog(this, "起点 word1:");
        String w2 = JOptionPane.showInputDialog(this, "终点 word2 (留空可计算到所有节点):");
        String res = graph.calcShortestPath(w1, w2);
        JOptionPane.showMessageDialog(this, res);
    }*/

    private void calcPageRank() {
        String w = JOptionPane.showInputDialog(this, "请输入单词以计算 PR 值：");
        if (w == null || w.isBlank()) {
            return;
        }
        double prValue = PageRankCalculator.getPageRankFor(
                w.toLowerCase(),
                graph.getGraph(),
                0.85
        );
        JOptionPane.showMessageDialog(
                this,
                String.format("%s 的 PageRank = %.5f", w, prValue)
        );
    }

    private void randomWalk() {
        RandomWalker walker = new RandomWalker(graph.getGraph());

        while (true) {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    walker.getPath() + "\n\n是否继续游走？",
                    "随机游走",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice != JOptionPane.YES_OPTION) break;

            String next = walker.step();
            if (next == null || !walker.canStep()) {
                JOptionPane.showMessageDialog(this, "已到达终点或无可用路径，游走结束。");
                break;
            }
        }

        // 展示完整路径
        String walk = walker.getPath();
        JOptionPane.showMessageDialog(this, "最终路径：\n" + walk);

        // 保存文件
        FileUtils.writeStringToFile(walk, "random_walk.txt");
        JOptionPane.showMessageDialog(this, "路径已保存到 random_walk.txt");
    }

}
