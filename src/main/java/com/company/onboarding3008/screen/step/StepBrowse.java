package com.company.onboarding3008.screen.step;

import io.jmix.ui.screen.*;
import com.company.onboarding3008.entity.Step;

@UiController("Step.browse")
@UiDescriptor("step-browse.xml")
@LookupComponent("stepsTable")
public class StepBrowse extends StandardLookup<Step> {
}