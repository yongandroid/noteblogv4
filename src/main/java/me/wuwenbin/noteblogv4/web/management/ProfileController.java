package me.wuwenbin.noteblogv4.web.management;

import cn.hutool.core.util.StrUtil;
import me.wuwenbin.noteblogv4.config.permission.NBAuth;
import me.wuwenbin.noteblogv4.dao.repository.ProfileRepository;
import me.wuwenbin.noteblogv4.model.entity.NBAbout;
import me.wuwenbin.noteblogv4.model.pojo.framework.LayuiTable;
import me.wuwenbin.noteblogv4.model.pojo.framework.NBR;
import me.wuwenbin.noteblogv4.model.pojo.framework.Pagination;
import me.wuwenbin.noteblogv4.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

import static me.wuwenbin.noteblogv4.config.permission.NBAuth.Group.AJAX;
import static me.wuwenbin.noteblogv4.config.permission.NBAuth.Group.ROUTER;
import static me.wuwenbin.noteblogv4.model.entity.permission.NBSysResource.ResType.NAV_LINK;

/**
 * created by Wuwenbin on 2018/12/16 at 13:15
 *
 * @author wuwenbin
 */
@Controller
@RequestMapping("/management")
public class ProfileController extends BaseController {

    private final ProfileRepository profileRepository;

    @Autowired
    public ProfileController(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @RequestMapping
    @NBAuth(value = "management:profile:page", remark = "关于tab内容管理页面", group = ROUTER, type = NAV_LINK)
    public String profile() {
        return "management/profile";
    }

    @RequestMapping("/list")
    @ResponseBody
    @NBAuth(value = "management:profile:list", remark = "关于tab内容管理分页数据", group = AJAX)
    public LayuiTable<NBAbout> aboutList(Pagination<NBAbout> aboutPagination) {
        Sort sort = getJpaSort(aboutPagination);
        Pageable pageable = PageRequest.of(aboutPagination.getPage() - 1, aboutPagination.getLimit(), sort);
        Page<NBAbout> page = profileRepository.findAll(pageable);
        return layuiTable(page, pageable);
    }

    @RequestMapping("/create")
    @NBAuth(value = "management:profile:create", remark = "添加关于关于tab内容操作", group = AJAX)
    @ResponseBody
    public NBR aboutCreate(NBAbout about) {
        if (about != null && StrUtil.isNotEmpty(about.getContent()) && StrUtil.isNotEmpty(about.getName())) {
            return ajaxDone(
                    () -> profileRepository.count(Example.of(NBAbout.builder().name(about.getName()).build())) == 0,
                    () -> ajaxDone(() -> profileRepository.save(about) != null, () -> "添加关于内容信息"),
                    () -> "已存在此关于tab内容"
            );
        }
        return NBR.error("添加关于tab模块内容有误！");
    }

    @RequestMapping("/delete")
    @NBAuth(value = "management:profile:delete", remark = "删除关于tab内容操作", group = AJAX)
    @ResponseBody
    public NBR delete(Long id) {
        return ajaxDone(id, profileRepository::deleteById, () -> "删除关于tab内容");
    }

    @RequestMapping("/update")
    @NBAuth(value = "management:profile:update", remark = "更新关于tab内容操作", group = AJAX)
    @ResponseBody
    public NBR profileUpdate(@Valid NBAbout about, BindingResult result) {
        if (result.getErrorCount() == 0) {
            return ajaxDone(
                    () -> profileRepository.count(Example.of(NBAbout.builder().name(about.getName()).build())) == 0,
                    () -> ajaxDone(() -> profileRepository.save(about) != null, () -> "修改关于tab内容信息"),
                    () -> "已存在此关于tab内容"
            );
        } else {
            return ajaxJsr303(result.getFieldErrors());
        }
    }
}
