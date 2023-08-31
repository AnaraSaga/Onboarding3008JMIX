package com.company.onboarding3008.screen.department;

import io.jmix.ui.screen.*;
import com.company.onboarding3008.entity.Department;

@UiController("Department.edit")
@UiDescriptor("department-edit.xml")
@EditedEntityContainer("departmentDc")
public class DepartmentEdit extends StandardEditor<Department> {
}