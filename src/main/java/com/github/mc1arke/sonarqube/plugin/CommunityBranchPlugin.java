/*
 * Copyright (C) 2020-2022 Michael Clarke
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package com.github.mc1arke.sonarqube.plugin;

import com.github.mc1arke.sonarqube.plugin.almclient.DefaultLinkHeaderReader;
import com.github.mc1arke.sonarqube.plugin.almclient.azuredevops.DefaultAzureDevopsClientFactory;
import com.github.mc1arke.sonarqube.plugin.almclient.bitbucket.DefaultBitbucketClientFactory;
import com.github.mc1arke.sonarqube.plugin.almclient.bitbucket.HttpClientBuilderFactory;
import com.github.mc1arke.sonarqube.plugin.almclient.github.DefaultGithubClientFactory;
import com.github.mc1arke.sonarqube.plugin.almclient.github.v3.DefaultUrlConnectionProvider;
import com.github.mc1arke.sonarqube.plugin.almclient.github.v3.RestApplicationAuthenticationProvider;
import com.github.mc1arke.sonarqube.plugin.almclient.github.v4.DefaultGraphqlProvider;
import com.github.mc1arke.sonarqube.plugin.almclient.gitlab.DefaultGitlabClientFactory;
import com.github.mc1arke.sonarqube.plugin.ce.CommunityReportAnalysisComponentProvider;
import com.github.mc1arke.sonarqube.plugin.scanner.*;
import com.github.mc1arke.sonarqube.plugin.scanner.autoconfiguration.*;
import com.github.mc1arke.sonarqube.plugin.server.CommunityBranchFeatureExtension;
import com.github.mc1arke.sonarqube.plugin.server.CommunityBranchSupportDelegate;
import com.github.mc1arke.sonarqube.plugin.server.MonoRepoFeature;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.validator.AzureDevopsValidator;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.validator.BitbucketValidator;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.validator.GithubValidator;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.validator.GitlabValidator;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.ws.binding.action.*;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.ws.pullrequest.PullRequestWs;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.ws.pullrequest.action.DeleteAction;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.ws.pullrequest.action.ListAction;
import org.sonar.api.CoreProperties;
import org.sonar.api.Plugin;
import org.sonar.api.PropertyType;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;
import org.sonar.core.config.PurgeConstants;
import org.sonar.core.extension.CoreExtension;

/**
 * @author Michael Clarke
 */
public class CommunityBranchPlugin implements Plugin, CoreExtension {

    public static final String IMAGE_URL_BASE = "com.github.mc1arke.sonarqube.plugin.branch.image-url-base";
    public static final String DRAFT_NOTE_ENABLED = "sonar.communityBranchPlugin.draftNoteEnabled";
    public static final String SEND_SUMMARY_NOTE = "sonar.communityBranchPlugin.sendSummaryNote";
    public static final String ANALYSIS_LANGUAGE_KEY = "sonar.communityBranchPlugin.language";

    public static final String ANALYSIS_LANGUAGE_DEFAULT_VALUE = "ru";

    public static final String PLUGIN_CATEGORY = "Community Branch";

    @Override
    public String getName() {
        return "Community Branch Plugin";
    }

    @Override
    public void load(CoreExtension.Context context) {
        if (SonarQubeSide.COMPUTE_ENGINE == context.getRuntime().getSonarQubeSide()) {
            context.addExtensions(CommunityReportAnalysisComponentProvider.class);
        } else if (SonarQubeSide.SERVER == context.getRuntime().getSonarQubeSide()) {
            context.addExtensions(CommunityBranchFeatureExtension.class, CommunityBranchSupportDelegate.class,
                                  DeleteBindingAction.class,
                                  SetGithubBindingAction.class,
                                  SetAzureBindingAction.class,
                                  SetBitbucketBindingAction.class,
                                  SetBitbucketCloudBindingAction.class,
                                  SetGitlabBindingAction.class,
                    ValidateBindingAction.class,
                    DeleteAction.class,
                    ListAction.class,
                    PullRequestWs.class,

                    GithubValidator.class,
                    DefaultGraphqlProvider.class,
                    DefaultGithubClientFactory.class,
                    DefaultLinkHeaderReader.class,
                    DefaultUrlConnectionProvider.class,
                    RestApplicationAuthenticationProvider.class,
                    HttpClientBuilderFactory.class,
                    DefaultBitbucketClientFactory.class,
                    BitbucketValidator.class,
                    GitlabValidator.class,
                    DefaultGitlabClientFactory.class,
                    DefaultAzureDevopsClientFactory.class,
                    AzureDevopsValidator.class,

                /* org.sonar.db.purge.PurgeConfiguration uses the value for the this property if it's configured, so it only
                needs to be specified here, but doesn't need any additional classes to perform the relevant purge/cleanup
                */
                                  PropertyDefinition
                                          .builder(PurgeConstants.DAYS_BEFORE_DELETING_INACTIVE_BRANCHES_AND_PRS)
                                          .name("Number of days before purging inactive branches and pull requests")
                                          .description(
                                                  "Branches and pull requests are permanently deleted when there has been no analysis for the configured number of days.")
                                          .category(CoreProperties.CATEGORY_HOUSEKEEPING)
                                          .subCategory(CoreProperties.SUBCATEGORY_BRANCHES_AND_PULL_REQUESTS).defaultValue("30")
                                          .type(PropertyType.INTEGER)
                                          .index(1)
                                          .build()
                                  ,

                                  PropertyDefinition
                                          .builder(PurgeConstants.BRANCHES_TO_KEEP_WHEN_INACTIVE)
                                          .name("Branches to keep when inactive")
                                          .description("By default, branches and pull requests are automatically deleted when inactive. This setting allows you "
                                                + "to protect branches (but not pull requests) from this deletion. When a branch is created with a name that "
                                                + "matches any of the regular expressions on the list of values of this setting, the branch will not be deleted "
                                                + "automatically even when it becomes inactive. Example:"
                                                + "<ul><li>develop</li><li>release-.*</li></ul>")
                                          .category(CoreProperties.CATEGORY_HOUSEKEEPING)
                                          .subCategory(CoreProperties.SUBCATEGORY_BRANCHES_AND_PULL_REQUESTS)
                                          .multiValues(true)
                                          .defaultValue("main,master,develop,trunk")
                                          .onQualifiers(Qualifiers.PROJECT)
                                          .index(2)
                                          .build()

                                 );

        }

        if (SonarQubeSide.COMPUTE_ENGINE == context.getRuntime().getSonarQubeSide() ||
            SonarQubeSide.SERVER == context.getRuntime().getSonarQubeSide()) {
            context.addExtensions(
                    PropertyDefinition.builder(IMAGE_URL_BASE)
                            .category(PLUGIN_CATEGORY)
                            .onQualifiers(Qualifiers.PROJECT)
                            .name("Images base URL")
                            .description("Base URL used to load the images for the PR comments (please use this only if images are not displayed properly).")
                            .type(PropertyType.STRING)
                            .build(),
                    PropertyDefinition.builder(DRAFT_NOTE_ENABLED)
                            .category(PLUGIN_CATEGORY)
                            .onQualifiers(Qualifiers.APP)
                            .name("Enable GitLab Draft notes")
                            .description("Includes drafts of notes in the GitLab request. This is necessary so that the letter receives several entries at a time.")
                            .type(PropertyType.BOOLEAN)
                            .build(),
                    PropertyDefinition.builder(SEND_SUMMARY_NOTE)
                            .category(PLUGIN_CATEGORY)
                            .onQualifiers(Qualifiers.PROJECT)
                            .name("Send summary note")
                            .description("При включенной опции, отправляет комментарий с общей информацией по результатам проверки мерж реквеста")
                            .type(PropertyType.BOOLEAN)
                            .build(),
                    PropertyDefinition.builder(ANALYSIS_LANGUAGE_KEY)
                            .category(PLUGIN_CATEGORY)
                            .onQualifiers(Qualifiers.PROJECT)
                            .name("Message language")
                            .description("Определяет на каком языке будут формироваться сообщения")
                            .options("ru", "en")
                            .type(PropertyType.SINGLE_SELECT_LIST)
                            .build(),
                    MonoRepoFeature.class);

        }
    }

    @Override
    public void define(Plugin.Context context) {
        if (SonarQubeSide.SCANNER == context.getRuntime().getSonarQubeSide()) {
            context.addExtensions(CommunityProjectBranchesLoader.class,
                                  CommunityBranchConfigurationLoader.class, CommunityBranchParamsValidator.class,
                                  ScannerPullRequestPropertySensor.class, BranchConfigurationFactory.class,
                                  AzureDevopsAutoConfigurer.class, BitbucketPipelinesAutoConfigurer.class,
                                  CirrusCiAutoConfigurer.class, CodeMagicAutoConfigurer.class,
                                  GithubActionsAutoConfigurer.class, GitlabCiAutoConfigurer.class,
                                  JenkinsAutoConfigurer.class);
        }
    }
}
