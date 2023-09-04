package com.company.onboarding3008.security;

import com.company.onboarding3008.entity.Department;
import com.company.onboarding3008.entity.User;
import io.jmix.security.role.annotation.JpqlRowLevelPolicy;
import io.jmix.security.role.annotation.RowLevelRole;

//defines role
@RowLevelRole(name = "HR mnger's dept and users",
        code = HRMngerSDeptAndUsersRole.CODE)
public interface HRMngerSDeptAndUsersRole {
    String CODE = "hr-mnger's-rl";

    // this annotation defines policy to read object
    @JpqlRowLevelPolicy(
            //entity for what the policy is applied
            entityClass = Department.class,
            //where should be added for each JPQL select
            //{E} is pseudoname of entity
            where = "{E}.hrMnger.id = :current_user_id")
    void department();

    @JpqlRowLevelPolicy(entityClass = User.class,
            where = "{E}.department.hrMnger.id = :current_user_id")
    void user();
}

