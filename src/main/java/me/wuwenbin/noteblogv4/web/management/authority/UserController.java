package me.wuwenbin.noteblogv4.web.management.authority;

import com.github.pagehelper.Page;
import me.wuwenbin.noteblogv4.config.permission.NBAuth;
import me.wuwenbin.noteblogv4.dao.mapper.UserMapper;
import me.wuwenbin.noteblogv4.dao.repository.RoleRepository;
import me.wuwenbin.noteblogv4.dao.repository.UserRepository;
import me.wuwenbin.noteblogv4.model.entity.permission.NBSysResource.ResType;
import me.wuwenbin.noteblogv4.model.entity.permission.NBSysRole;
import me.wuwenbin.noteblogv4.model.entity.permission.NBSysUser;
import me.wuwenbin.noteblogv4.model.pojo.framework.LayuiTable;
import me.wuwenbin.noteblogv4.model.pojo.framework.NBR;
import me.wuwenbin.noteblogv4.model.pojo.framework.Pagination;
import me.wuwenbin.noteblogv4.service.users.UsersService;
import me.wuwenbin.noteblogv4.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * created by Wuwenbin on 2018/7/28 at 23:37
 * @author wuwenbin
 */
@Controller
@RequestMapping("/management/users")
public class UserController extends BaseController {

    private final UsersService usersService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Autowired
    public UserController(UsersService usersService, UserRepository userRepository, UserMapper userMapper, RoleRepository roleRepository) {
        this.usersService = usersService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
    }

    @RequestMapping
    @NBAuth(value = "management:user_list:router", remark = "用户管理界面", type = ResType.NAV_LINK, group = "management:user")
    public String index() {
        return "management/users";
    }


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    @NBAuth(value = "management:user_list:ajax", remark = "用户信息分页数据", group = "management:user")
    public LayuiTable<NBSysUser> list(Pagination<NBSysUser> userPage, NBSysUser user) {
        Page<NBSysUser> pageUser = usersService.findUserPage(userPage, user);
        return layuiTable(pageUser);
    }


    @RequestMapping(value = "/edit/enable", method = RequestMethod.POST)
    @ResponseBody
    @NBAuth(value = "management:user_edit_enable:ajax", remark = "修改用户状态信息", group = "management:user")
    public NBR editEnable(Long id, Boolean enable) {
        return ajaxDone(
                () -> userRepository.updateUserStatus(id, enable) > 0,
                () -> "修改用户状态"
        );
    }

    @RequestMapping("/roles/list")
    @ResponseBody
    @NBAuth(value = "management:user_roles_list:ajax", remark = "查询用户的角色信息", group = "management:user")
    public List<NBSysRole> roleList(Long userId) {
        if (StringUtils.isEmpty(userId)) {
            return roleRepository.findAll();
        } else {
            return userMapper.findUserRoles(userId);
        }
    }

    @RequestMapping("/roles/update")
    @ResponseBody
    @NBAuth(value = "management:user_roles_update:ajax", remark = "修改用户的角色关联信息", group = "management:user")
    public NBR updateUserRoles(Long userId, String roleIds) {
        if (StringUtils.isEmpty(roleIds)) {
            return NBR.error("角色信息为空，至少选择一个角色信息！");
        } else if (StringUtils.isEmpty(userId)) {
            return NBR.error("用户信息为空，请勾选用户！");
        } else {
            usersService.updateUserRoles(userId, roleIds);
            return NBR.ok("更新用户角色信息成功！");
        }
    }

}
