package com.company.onboarding3008.screen.department;

import io.jmix.ui.screen.*;
import com.company.onboarding3008.entity.Department;

@UiController("Department.browse")
@UiDescriptor("department-browse.xml")
@LookupComponent("departmentsTable")
public class DepartmentBrowse extends StandardLookup<Department> {
}