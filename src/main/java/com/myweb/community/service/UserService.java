package com.myweb.community.service;

import com.myweb.community.dao.LoginTicketMapper;
import com.myweb.community.dao.UserMapper;
import com.myweb.community.entity.LoginTicket;
import com.myweb.community.entity.User;
import com.myweb.community.util.CommunityConstant;
import com.myweb.community.util.CommunityUtil;
import com.myweb.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.Thymeleaf;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    /**
     * 根据userid搜索user
     * @param id
     * @return
     */
    public User findUserById(int id) {
        return userMapper.selectById(id);
    }


    /**
     * 注册 插入用户
     * @param user
     * @return
     */
    public Map<String,Object> register(User user){
        Map<String,Object> map=new HashMap<>();
        if(user==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
        }
        if(StringUtils.isBlank(user.getNumber())){
            map.put("numberMsg","学号不能为空");
        }
        User user1=userMapper.selectByName(user.getUsername());
        if (user1!=null){
            map.put("usernameMsg","账号已注册");
            return map;
        }

        User user2=userMapper.selectByEmail(user.getEmail());

        if(user2!=null){
            map.put("emailMsg","邮箱已注册");
            return map;
        }
        user.setSalt(CommunityUtil.getUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.getUUID());
        user.setHeaderUrl("https://wx2.sinaimg.cn/mw690/007W4zAXgy1ggx46k1lksj30hs0hs74c.jpg");
        user.setCreateTime(new Date());

        userMapper.insertUser(user);

        //发送邮件
        Context context=new Context();
        context.setVariable("email",user.getEmail());

        String url=domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();

        context.setVariable("url",url);

        String content=templateEngine.process("/mail/activation",context);

        mailClient.sendMail(user.getEmail(),"激活邮件",content);



        return map;

    }

    /**
     * 用户激活
     * @param userid
     * @param code
     * @return
     */
    public int activation(int userid,String code){
        User user=userMapper.selectById(userid);
        if(user.getStatus()==1) {
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userid,1);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FALLURE;
        }
    }

    /**
     * 登录
     * @param username
     * @param password
     * @param expiredSeconds
     * @return
     */
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }

        // 验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在!");
            return map;
        }

        // 验证状态
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活!");
            return map;
        }

        // 验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确!");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.getUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }
}
