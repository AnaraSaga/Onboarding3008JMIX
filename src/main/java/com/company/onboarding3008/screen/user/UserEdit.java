package com.company.onboarding3008.screen.user;

import com.company.onboarding3008.entity.OnBoardingStatus;
import com.company.onboarding3008.entity.Step;
import com.company.onboarding3008.entity.User;
import com.company.onboarding3008.entity.UserStep;
import io.jmix.core.DataManager;
import io.jmix.core.EntityStates;
import io.jmix.core.security.event.SingleUserPasswordChangeEvent;
import io.jmix.ui.Notifications;
import io.jmix.ui.UiComponents;
import io.jmix.ui.component.*;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.model.CollectionPropertyContainer;
import io.jmix.ui.model.DataContext;
import io.jmix.ui.model.InstanceContainer;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

@UiController("User.edit")
@UiDescriptor("user-edit.xml")
@EditedEntityContainer("userDc")
@Route(value = "users/edit", parentPrefix = "users")
public class UserEdit extends StandardEditor<User> {

    @Autowired
    private EntityStates entityStates;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordField passwordField;

    @Autowired
    private TextField<String> usernameField;

    @Autowired
    private PasswordField confirmPasswordField;

    @Autowired
    private Notifications notifications;

    @Autowired
    private MessageBundle messageBundle;

    @Autowired
    private ComboBox<String> timeZoneField;

    @Autowired
    private DataContext dataContext;

    @Autowired
    private DataManager dataManager;

    @Autowired
    private CollectionPropertyContainer<UserStep> stepsDc;



    private boolean isNewEntity;

    @Subscribe
    public void onInit(InitEvent event) {
        timeZoneField.setOptionsList(Arrays.asList(TimeZone.getAvailableIDs()));
    }

    @Subscribe
    public void onInitEntity(InitEntityEvent<User> event) {
        usernameField.setEditable(true);
        passwordField.setVisible(true);
        confirmPasswordField.setVisible(true);
        isNewEntity = true;

        //set onboarding status by default
        User user = event.getEntity();
        user.setOnboardingStatus(OnBoardingStatus.NOT_STARTED);
    }

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {
        if (entityStates.isNew(getEditedEntity())) {
            usernameField.focus();
        }
    }

    @Subscribe
    protected void onBeforeCommit(BeforeCommitChangesEvent event) {
        if (entityStates.isNew(getEditedEntity())) {
            if (!Objects.equals(passwordField.getValue(), confirmPasswordField.getValue())) {
                notifications.create(Notifications.NotificationType.WARNING)
                        .withCaption(messageBundle.getMessage("passwordsDoNotMatch"))
                        .show();
                event.preventCommit();
            }
            getEditedEntity().setPassword(passwordEncoder.encode(passwordField.getValue()));
        }
    }

    @Subscribe(target = Target.DATA_CONTEXT)
    public void onPostCommit(DataContext.PostCommitEvent event) {
        if (isNewEntity) {
            getApplicationContext().publishEvent(new SingleUserPasswordChangeEvent(getEditedEntity().getUsername(), passwordField.getValue()));
        }
    }

    @Subscribe("generateButton")
    public void onGenerateButtonClick( Button.ClickEvent event) {
        // to get user for further edit data in user
        User user = getEditedEntity();

        // if joining date is not set, show msg and break
        if(user.getJoiningDate() == null) {
            notifications.create()
                    .withCaption("Joining date is not set")
                    .show();
            return;
        }

        //Load list of steps
        List < Step> steps = dataManager.load(Step.class)
                .query("Select s from Step s order by s.sortValue asc ")
                .list();

        //if Step is already in stepsDC , then pass it
        for (Step step : steps) {
            if(stepsDc.getItems()
                    .stream().
                    noneMatch(userStep -> userStep.getStep()
                            .equals(step))){
                // create new userStep using DataContext.create
                UserStep userStep = dataContext.create(UserStep.class);

                userStep.setUser(user);
                userStep.setStep(step);
                userStep.setDueDate(user.getJoiningDate().plusDays(step.getDuration()));
                userStep.setSortValue(step.getSortValue());
                // add new UserStep in collection stepsDc and show in UI
                stepsDc.getMutableItems().add(userStep);
            }
        }

    }

    // for column done in steps

    @Autowired
    private UiComponents uiComponents;

    @Install(to = "stepsTable.done", subject = "columnGenerator")
    //column generator receives entity and show it in table as an argument
    private Component stepsTableDoneColumnGenerator(final UserStep userStep) {
        //create checkbox using UIComponents
        CheckBox checkBox = uiComponents.create(CheckBox.class);
        checkBox.setValue(userStep.getCompletedDate() != null);
        // press on flag => flag changes meaning => flag calls its listener ValueChangeEvent
        // => listener set attribute completedDate by UserStep
        checkBox.addValueChangeListener(e -> {
            if (userStep.getCompletedDate() == null){
                userStep.setCompletedDate(LocalDate.now());
            } else {
                userStep.setCompletedDate(null);
            }
        });
        //return visual component and shown in column
        return checkBox;
    }

    //CHANGES IN ONBOARDING STATUS:

    //ANY Changes
    @Subscribe(id = "stepsDc", target = Target.DATA_CONTAINER)
    public void onStepsDcItemPropertyChange(final InstanceContainer.ItemPropertyChangeEvent<UserStep> event) {
        
    }

    //Add delete elements
    @Subscribe(id = "stepsDc", target = Target.DATA_CONTAINER)
    public void onStepsDcCollectionChange(final CollectionContainer.CollectionChangeEvent<UserStep> event) {

    }

    private void updateOnboardingStatus(){
        User user = getEditedEntity();

        //get edited user
        long completedCount = user.getSteps() == null ? 0 :
                user.getSteps().stream()
                        .filter(us -> us.getCompletedDate() != null)
                        .count();

//        if (user.getSteps() == null) {
//            long comlpletedCount = 0;
//        } else {
//            long comlpletedCount = user.getSteps().stream()
//                    .filter(us -> us.getCompletedDate() != null)
//                    .count();
//        }

        //Update status.
        if(completedCount == 0){
            user.setOnboardingStatus(OnBoardingStatus.NOT_STARTED);
        } else if (completedCount == user.getSteps().size()){
            user.setOnboardingStatus(OnBoardingStatus.COMPLETED);
        } else {
            user.setOnboardingStatus(OnBoardingStatus.IN_PROGRESS);
        }





    }



}