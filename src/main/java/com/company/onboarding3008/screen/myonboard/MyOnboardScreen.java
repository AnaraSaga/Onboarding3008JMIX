package com.company.onboarding3008.screen.myonboard;

import com.company.onboarding3008.entity.User;
import com.company.onboarding3008.entity.UserStep;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.UiComponents;
import io.jmix.ui.component.CheckBox;
import io.jmix.ui.component.Component;
import io.jmix.ui.component.Label;
import io.jmix.ui.component.Table;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.model.InstanceContainer;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@UiController("MyOnboardScreen")
@UiDescriptor("my-onboard-screen.xml")
public class MyOnboardScreen extends Screen {

    @Autowired
    private CurrentAuthentication currentAuthentication;

    @Autowired
    private CollectionLoader<UserStep> userStepsDl;

    @Autowired
    private UiComponents uiComponents;

    @Autowired
    private Label totalStepsLabel;

    @Autowired
    private Label<String> completedStepsLabel;

    @Autowired
    private Label overdueStepsLabel;

    @Autowired
    private CollectionContainer <UserStep> userStepsDc;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        final User user = (User) currentAuthentication.getUser();
        userStepsDl.setParameter("user", user);
        userStepsDl.load();
        updateLabels();
    }

    @Install (to = "userStepsTable.done", subject = "columnGenerator")
    private Component userStepsTableCompletedColumnGenerator (UserStep userStep){
        CheckBox checkBox = uiComponents.create(CheckBox.class);
        checkBox.setValue(userStep.getCompletedDate() != null);
        checkBox.addValueChangeListener(e -> {
            if (userStep.getCompletedDate() == null){
                userStep.setCompletedDate(LocalDate.now());
            } else {
                userStep.setCompletedDate(null);
            }
        });
        return checkBox;
    }

    private void updateLabels(){
        totalStepsLabel.setValue("Total steps: " + userStepsDc.getItems().size());

        long completedCount = userStepsDc.getItems().stream()
                .filter(us -> us.getCompletedDate() != null)
                .count();
        completedStepsLabel.setValue("Completed steps");

        long overdueCount = userStepsDc.getItems().stream()
                .filter(us -> isOverdue(us))
                .count();
        overdueStepsLabel.setValue("Overdue steps: " + overdueCount);
    }

    private boolean isOverdue (UserStep us){
        return us.getCompletedDate() == null
                && us.getDueDate() != null
                && us.getDueDate().isBefore(LocalDate.now());
    }

    @Subscribe(id = "userStepsDc", target = Target.DATA_CONTAINER)
    public void onUserStepsDcItemPropertyChange(final InstanceContainer.ItemPropertyChangeEvent<UserStep> event) {
        updateLabels();
    }

    //to add column in table:
//    @Install(to = "userStepsTable.kana", subject = "valueProvider")
//    private Object userStepsTableKanaValueProvider(final UserStep userStep) {
//        return "oops " + userStep.getStep().getName();
//    }

//    @Install(to = "userStepsTable.kana", subject = "columnGenerator")
//    private Component userStepsTableKanaColumnGenerator(final UserStep userStep) {
//        Label<String> label = uiComponents.create(Label.NAME);
//        label.setValue("opps - " + userStep.getStep().getName());
//        return label;
//    }


}