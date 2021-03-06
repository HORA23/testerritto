package com.netcracker.testerritto.dao;

import com.netcracker.testerritto.mappers.GroupRowMapper;
import com.netcracker.testerritto.mappers.UserRowMapper;
import com.netcracker.testerritto.models.Group;
import com.netcracker.testerritto.models.User;
import com.netcracker.testerritto.properties.AttrtypeProperties;
import com.netcracker.testerritto.properties.ObjtypeProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Transactional
public class UserDAO {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRowMapper userRowMapper;

    final String QUERY_IS_USER_ATTRIBUTES_EXIST =
        "select count(*) " +
            "from attributes " +
            "where attr_id = ? " +
            "and value = ?";

    private final static String IS_USER_CONSIST_IN_GROUP =
        "select count(*)  "
        +"from "
        +"   objreference user2group "
        +"where "
        +"   user2group.attr_id = 22 /* consist */ "
        +"   and user2group.object_id = ? /* user_id */ "
        +"   and user2group.reference = ? /*group_id*/          ";

    final String QUERY_GET_USER_BY_EMAIL =
        "select " +
            "    user_last_name.value, " +
            "    user_first_name.value,\n" +
            "    user_password.value," +
            "    user_phone.value, " +
            "    user_last_name.object_id     \n" +
            "from " +
            "    attributes user_last_name," +
            "    attributes user_first_name," +
            "    attributes users_email,\n" +
            "    attributes user_password," +
            "    attributes user_phone\n" +
            "where " +
            "    users_email.value = ? " +
            "and user_last_name.attr_id = 1 /*LAST_NAME*/ \n" +
            "and user_last_name.object_id = users_email.object_id \n" +
            "and user_first_name.attr_id = 2 /*FIRST_NAME*/    \n" +
            "and user_first_name.object_id = users_email.object_id \n" +
            "and user_password.attr_id = 4   /*PASSWORD*/ \n" +
            "and user_password.object_id = users_email.object_id \n" +
            "and user_phone.attr_id = 5     /*PHONE*/ \n" +
            "and user_phone.object_id = users_email.object_id";

    final String QUERY_GET_CREATED_GROUPS =
        "select" +
            "    groups_name.object_id id," +
            "    groups_name.value name," +
            "    groups_link.value link," +
            "    users.object_id creator_id\n" +
            "from   " +
            "    objects users," +
            "    objects groups," +
            "    attributes groups_link,   \n" +
            "    attributes groups_name," +
            "    objreference groups_2_user       \n" +
            "where " +
            "    users.object_id =  ?                                 \n" +
            "and groups.object_type_id = 2                            \n" +
            "and groups_name.object_id = GROUPS.object_id     \n" +
            "and groups_name.attr_id = 6         /*groupName*/\n" +
            "and groups_link.object_id = GROUPS.object_id     \n" +
            "and groups_link.attr_id = 7         /*groupLink*/\n" +
            "and groups_2_user.attr_id = 25      /*CREATE_GROUP_BY*/\n" +
            "and groups_2_user.object_id = GROUPS.object_id   \n" +
            "and groups_2_user.reference = USERS.object_id ";

    final String QUERY_GET_GROUPS =
        "select" +
            "     groups.object_id id,\n" +
            "     groups_name.value name,  \n" +
            "     groups_link.value link,\n" +
            "     creator.object_id creator_id\n" +
            "from" +
            "     objects groups,\n" +
            "     attributes groups_name, \n" +
            "     attributes groups_link, \n" +
            "     objreference user_2_groups,\n" +
            "     objects creator,\n" +
            "     objreference group_2_creator\n" +
            "where" +
            "     groups.object_type_id = 2 \n" +
            "and  groups_name.attr_id = 6 /*NAME_GROUP*/\n" +
            "and  groups_name.object_id = groups.object_id \n" +
            "and  groups_link.attr_id = 7/*LINK*/      \n" +
            "and  groups_link.object_id = groups.object_id \n" +
            "and  user_2_groups.attr_id = 22 /*CONSIST*/   \n" +
            "and  user_2_groups.object_id = ? /*USER_ID*/   \n" +
            "and  user_2_groups.reference = groups.object_id\n" +
            "and  group_2_creator.attr_id = 25\n" +
            "and  group_2_creator.object_id = groups.object_id\n" +
            "and  creator.object_id = group_2_creator.reference";

    final String QUERY_GET_USER =
        "select \n" +
            "    users.object_id id,\n" +
            "    user_last_name.value last_name,\n" +
            "    user_firs_name.value first_name ,\n" +
            "    user_email.value email,   \n" +
            "    user_password.value password,\n" +
            "    user_phone.value phone \n" +
            "from " +
            "    attributes user_last_name,\n" +
            "    attributes user_firs_name,\n" +
            "    attributes user_email,\n" +
            "    attributes user_password,\n" +
            "    attributes user_phone,\n" +
            "    objects users \n" +
            "where  " +
            "    users.object_id = ?\n" +
            "and user_last_name.attr_id = 1         /*LAST_NAME*/   \n" +
            "and user_last_name.object_id = users.object_id         \n" +
            "and user_firs_name.attr_id = 2         /*FIRST_NAME*/  \n" +
            "and user_firs_name.object_id = users.object_id         \n" +
            "and user_email.attr_id =3             /*EMAIL*/       \n" +
            "and user_email.object_id = users.object_id             \n" +
            "and user_password.attr_id = 4          /*PASSWORD*/    \n" +
            "and user_password.object_id = users.object_id          \n" +
            "and user_phone.attr_id = 5             /*PHONE*/       \n" +
            "and user_phone.object_id = users.object_id";

    final String QUERY_GET_USER_BY_PHONE =
        "select  \n" +
            "    user_last_name.object_id id, \n" +
            "    user_last_name.value last_name,  \n" +
            "    user_first_name.value first_name, \n" +
            "    user_email.value email,  \n" +
            "    user_password.value password, \n" +
            "    user_phone.value phone \n" +
            "from \n" +
            "    attributes user_last_name,\n" +
            "    attributes user_first_name,\n" +
            "    attributes user_email,\n" +
            "    attributes user_password,\n" +
            "    attributes user_phone \n" +
            "where    \n" +
            "    user_phone.value =  ? \n" +
            "and user_last_name.attr_id = 1    /*LAST_NAME*/ \n" +
            "and user_last_name.object_id = user_phone.object_id \n" +
            "and user_first_name.attr_id = 2   /*FIRST_NAME*/    \n" +
            "and user_first_name.object_id = user_phone.object_id\n" +
            "and user_password.attr_id = 4     /*PASSWORD*/ \n" +
            "and user_password.object_id = user_phone.object_id \n" +
            "and user_email.attr_id = 3        /*EMAIL*/ \n" +
            "and user_email.object_id = user_phone.object_id \n";


    public User getUser(BigInteger id) {

        return jdbcTemplate.queryForObject(QUERY_GET_USER, new Object[]{id.toString()}, new UserRowMapper());
    }

    public void deleteUser(BigInteger id) {

        new ObjectEavBuilder.Builder(jdbcTemplate)
            .setObjectId(id)
            .delete();
    }

    @Deprecated
    public BigInteger createUser(String firstName, String lastName, String email,
                                 String password, String phone) {

        String objectName = firstName + " " + lastName;

        return new ObjectEavBuilder.Builder((jdbcTemplate))
            .setObjectTypeId(ObjtypeProperties.USER)
            .setName(objectName)
            .setStringAttribute(AttrtypeProperties.LAST_NAME, lastName)
            .setStringAttribute(AttrtypeProperties.FIRST_NAME,
                firstName)
            .setStringAttribute(AttrtypeProperties.EMAIL, email)
            .setStringAttribute(AttrtypeProperties.PASSWORD, password)
            .setStringAttribute(AttrtypeProperties.PHONE, phone)
            .create();
    }

    public BigInteger createUser(User user) {
        String objectName = user.getFirstName() + " " + user.getLastName();

        return new ObjectEavBuilder.Builder((jdbcTemplate))
            .setObjectTypeId(new BigInteger(String.valueOf(ObjtypeProperties.USER)))
            .setName(objectName)
            .setStringAttribute(new BigInteger(String.valueOf(AttrtypeProperties.LAST_NAME)),
                user.getLastName())
            .setStringAttribute(new BigInteger(String.valueOf(AttrtypeProperties.FIRST_NAME)),
                user.getFirstName())
            .setStringAttribute(new BigInteger(String.valueOf(AttrtypeProperties.EMAIL)),
                user.getEmail())
            .setStringAttribute(new BigInteger(String.valueOf(AttrtypeProperties.PASSWORD)),
                user.getPassword())
            .setStringAttribute(new BigInteger(String.valueOf(AttrtypeProperties.PHONE)),
                user.getPhone())
            .create();
    }

    @Deprecated
    public void updateLastName(BigInteger id, String lastName) {

        new ObjectEavBuilder.Builder(jdbcTemplate)
            .setObjectId(id)
            .setStringAttribute(AttrtypeProperties.LAST_NAME, lastName)
            .update();

    }

    @Deprecated
    public void updateFirstName(BigInteger id, String firstName) {
        new ObjectEavBuilder.Builder(jdbcTemplate)
            .setObjectId(id)
            .setStringAttribute(AttrtypeProperties.FIRST_NAME, firstName)
            .update();
    }

    @Deprecated
    public void updateEmail(BigInteger id, String email) {
        new ObjectEavBuilder.Builder(jdbcTemplate)
            .setObjectId(id)
            .setStringAttribute(new BigInteger(String.valueOf(AttrtypeProperties.EMAIL)), email)
            .update();

    }

    @Deprecated
    public void updatePassword(BigInteger id, String password) {
        new ObjectEavBuilder.Builder(jdbcTemplate)
            .setObjectId(id)
            .setStringAttribute(new BigInteger(String.valueOf(AttrtypeProperties.PASSWORD)), password)
            .update();

    }

    @Deprecated
    public void updatePhone(BigInteger id, String phone) {
        new ObjectEavBuilder.Builder(jdbcTemplate)
            .setObjectId(id)
            .setStringAttribute(new BigInteger(String.valueOf(AttrtypeProperties.PHONE)), phone)
            .update();

    }

    public List<Group> getGroups(BigInteger id) {
        List<Group> groups = jdbcTemplate.query(QUERY_GET_GROUPS, new Object[]{id.toString()}, new GroupRowMapper());
        return groups;
    }

    public List<Group> getCreatedGroups(BigInteger id) {
        List<Group> listGroups = jdbcTemplate.query(QUERY_GET_CREATED_GROUPS,
            new Object[]{id.toString()}, new GroupRowMapper());

        return listGroups;
    }

    @Deprecated
    public void deleteCreatedGroup(BigInteger userId, BigInteger createdGroupId) {

        new ObjectEavBuilder.Builder(jdbcTemplate)
            .setObjectId(createdGroupId)
            .setReference(AttrtypeProperties.CREATE_GROUP_BY, userId)
            .delete();
    }

    public void enterInGroup(BigInteger userId, BigInteger groupId) {

        new ObjectEavBuilder.Builder(jdbcTemplate)
            .setObjectId(userId)
            .setReference(AttrtypeProperties.CONSIST, groupId)
            .createReference();

    }

    public void exitFromGroup(BigInteger userId, BigInteger groupId) {

        new ObjectEavBuilder.Builder(jdbcTemplate)
            .setObjectId(userId)
            .setReference(AttrtypeProperties.CONSIST, groupId)
            .delete();

    }

    public User getUserByEmail(String email) {
        return jdbcTemplate.query(QUERY_GET_USER_BY_EMAIL, new Object[]{email}, new ResultSetExtractor<User>() {
            @Override
            public User extractData(ResultSet resultSet) throws SQLException, DataAccessException {

                if (resultSet.next()) {
                    User user = new User();
                    user.setLastName(resultSet.getString(1));
                    user.setFirstName(resultSet.getString(2));
                    user.setPassword(passwordEncoder.encode(resultSet.getString(3)));
                    user.setPhone(resultSet.getString(4));
                    user.setId(new BigInteger(resultSet.getString(5)));
                    user.setEmail(email);
                    return user;
                }
                return null;
            }
        });
    }

    public User getUserByPhone(String phone) {
        return jdbcTemplate.queryForObject(QUERY_GET_USER_BY_PHONE, new Object[]{phone}, userRowMapper);
    }

    public boolean isEmailExist(String email) {
        Integer i = jdbcTemplate.queryForObject(QUERY_IS_USER_ATTRIBUTES_EXIST,
            new Object[]{3, email}, Integer.class);
        return (i == 1);
    }

    public boolean isPhoneExist(String phone) {
        Integer i = jdbcTemplate.queryForObject(QUERY_IS_USER_ATTRIBUTES_EXIST,
            new Object[]{5, phone}, Integer.class);
        return (i == 1);
    }

    public boolean isUserConsistInGroup(BigInteger userId, BigInteger groupId) {
        Integer i = jdbcTemplate.queryForObject(IS_USER_CONSIST_IN_GROUP, new Object[]{userId.toString(), groupId.toString()}, Integer.class);
        return (i == 1);
    }


    public User updateUser(User user) {

        new ObjectEavBuilder.Builder(jdbcTemplate)
            .setObjectId(user.getId())
            .setObjectTypeId(ObjtypeProperties.USER)
            .setStringAttribute(AttrtypeProperties.LAST_NAME, user.getLastName())
            .setStringAttribute(AttrtypeProperties.FIRST_NAME, user.getFirstName())
            .setStringAttribute(AttrtypeProperties.PHONE, user.getPhone())
            .update();

        return user;

    }

    public User updateEmailAndPassword(User user) {

        new ObjectEavBuilder.Builder(jdbcTemplate)
            .setObjectId(user.getId())
            .setStringAttribute(AttrtypeProperties.EMAIL, user.getEmail())
            .setStringAttribute(AttrtypeProperties.PASSWORD, user.getPassword())
            .update();

        return user;

    }

    public User updatePassword(User user) {
        new ObjectEavBuilder.Builder(jdbcTemplate)
            .setObjectId(user.getId())
            .setStringAttribute(AttrtypeProperties.PASSWORD, user.getPassword())
            .update();
        return user;
    }
}



