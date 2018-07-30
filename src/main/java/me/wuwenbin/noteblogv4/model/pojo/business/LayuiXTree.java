package me.wuwenbin.noteblogv4.model.pojo.business;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * created by Wuwenbin on 2018/7/23 at 16:55
 *
 * @author wuwenbin
 */
@Data
public class LayuiXTree implements Serializable {

    private String id;
    private String title;
    private String value;
    private String parentId;
    private Boolean checked;
    private Boolean disabled;
    private List<LayuiXTree> data = new ArrayList<>();

    public LayuiXTree(String title, String value, String id, boolean checked, boolean disabled) {
        this.title = title;
        this.value = value;
        this.id = id;
        this.checked = checked;
        this.disabled = disabled;
        this.parentId = getPid(id);
    }

    public LayuiXTree(String id, String parentId) {
        this.title = "";
        this.value = "";
        this.id = id;
        this.checked = false;
        this.disabled = false;
        this.parentId = parentId;
    }


    private static String getPid(String gp) {
        if (StringUtils.isEmpty(gp)) {
            throw new RuntimeException("title 不能为空！");
        } else {
            String[] groupArray = gp.split(":");
            int length = groupArray.length;
            int middleLevel = 2;
            if (length == 1) {
                return "root";
            } else if (length == middleLevel) {
                return groupArray[0];
            } else {
                return groupArray[1];
            }
        }
    }

    private static LayuiXTree findChildren(LayuiXTree treeNode, List<LayuiXTree> treeNodes) {
        for (LayuiXTree it : treeNodes) {
            if (treeNode.getId().equals(it.getParentId())) {
                if (treeNode.getData() == null) {
                    treeNode.setData(new ArrayList<>());
                }
                treeNode.getData().add(findChildren(it, treeNodes));
            }
        }
        return treeNode;
    }

    /**
     * 使用递归方法建树
     *
     * @param treeNodes
     * @return
     */
    public static List<LayuiXTree> buildByRecursive(List<LayuiXTree> treeNodes) {
        List<LayuiXTree> trees = new ArrayList<>(treeNodes);
        treeNodes.forEach(node -> {
            LayuiXTree xTree = new LayuiXTree(node.getParentId(), getPid(node.getParentId()));
            Arrays.stream(node.getId().split(":")).forEach(id -> {
                long cnt1 = trees.stream().filter(tree -> tree.getId().equalsIgnoreCase(id)).count();
                if (cnt1 == 0) {
                    LayuiXTree xTree1 = new LayuiXTree("", "", id, false, false);
                    trees.add(xTree1);
                }
            });
        });
        for (LayuiXTree treeNode : treeNodes) {
            if ("root".equals(treeNode.getParentId())) {
                trees.add(findChildren(treeNode, treeNodes));
            }
        }
        return trees;
    }
}
