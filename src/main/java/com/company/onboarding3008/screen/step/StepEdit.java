package com.company.onboarding3008.screen.step;

import io.jmix.ui.screen.*;
import com.company.onboarding3008.entity.Step;

@UiController("Step.edit")
@UiDescriptor("step-edit.xml")
@EditedEntityContainer("stepDc")
public class StepEdit extends StandardEditor<Step> {
}