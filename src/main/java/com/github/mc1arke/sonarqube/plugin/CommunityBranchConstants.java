package com.github.mc1arke.sonarqube.plugin;

public final class CommunityBranchConstants {

    private CommunityBranchConstants() {}

    // Для сообщения в комментарии по коду в мерж реквесте
    public static final String DECORATOR_TYPE = "decorator.communityBranch.Type";
    public static final String DECORATOR_SEVERITY = "decorator.communityBranch.Severity";
    public static final String DECORATOR_MESSAGE = "decorator.communityBranch.Message";
    public static final String DECORATOR_VIEW_IN_SONAR = "decorator.communityBranch.viewInSonarQube";
    public static final String DECORATOR_PROJECT_ID = "decorator.communityBranch.projectId";
    public static final String DECORATOR_ISSUE_ID = "decorator.communityBranch.issueId";

}
