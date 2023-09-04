package com.company.onboarding3008.screen.myonboard;

import com.company.onboarding3008.entity.User;
import com.company.onboarding3008.entity.UserStep;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.UiComponents;
import io.jmix.ui.component.*;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.model.DataContext;
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

    @Autowired
    private DataContext dataContext;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        final User user = (User) currentAuthentication.getUser();
        userStepsDl.setParameter("user", user);
        userStepsDl.load();
        updateLabels();
    }

// to color the overdue dates
//    @Install(to = "userStepsTable", subject = "styleProvider")
//    //Style provider get entity and show in table as an argument
//    private String userStepsTableStyleProvider(final UserStep entity, final String property) {
//        //if duedate => this step is overdue
//        if ("duedate".equals(property) && isOverdue(entity)){
//            return "overdue-step";
//        }
//        // if not overdue then style is by default
//        return null;
//    }

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
        completedStepsLabel.setValue("Completed steps: " + completedCount);

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

    @Subscribe("saveButton")
    public void onSaveButtonClick(final Button.ClickEvent event) {
        //DataContext track changes in Entity
        //when ....commit => all changes save in DB
        dataContext.commit();

        //....close => close screen.
        // StandardOutcome - is an object to be analyzed by .close
        close(StandardOutcome.COMMIT);
    }

    @Subscribe("discardButton")
    public void onDiscardButtonClick(final Button.ClickEvent event) {
        close(StandardOutcome.DISCARD);
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