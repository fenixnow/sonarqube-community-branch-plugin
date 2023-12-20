/*
 * Copyright (C) 2021 Michael Clarke
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
package com.github.mc1arke.sonarqube.plugin.almclient.gitlab;

import com.github.mc1arke.sonarqube.plugin.almclient.gitlab.model.*;

import java.io.IOException;
import java.util.List;

public interface GitlabClient {

    User getCurrentUser() throws IOException;

    MergeRequest getMergeRequest(String projectId, long mergeRequestIid) throws IOException;

    List<Commit> getMergeRequestCommits(long projectId, long mergeRequestIid) throws IOException;

    List<Discussion> getMergeRequestDiscussions(long projectId, long mergeRequestIid) throws IOException;

    Discussion addMergeRequestDiscussion(long projectId, long mergeRequestIid, MergeRequestNote commitNote) throws IOException;

    void addMergeRequestDiscussionNote(long projectId, long mergeRequestIid, String discussionId, String noteContent) throws IOException;

    void resolveMergeRequestDiscussion(long projectId, long mergeRequestIid, String discussionId) throws IOException;

    void setMergeRequestPipelineStatus(long projectId, String commitRevision, PipelineStatus status) throws IOException;

    /**
     * Create a draft note
     * {@link } <a href="https://docs.gitlab.com/ee/api/draft_notes.html#create-a-draft-note">docs.gitlab</a>
     * @param projectId
     * @param mergeRequestIid
     * @param mergeRequestNote
     */
    DraftNote addMergeRequestDraftNotes(long projectId, long mergeRequestIid, MergeRequestNote mergeRequestNote) throws IOException;

    /**
     * Publish all pending draft notes
     * {@link } <a href="https://docs.gitlab.com/ee/api/draft_notes.html#publish-all-pending-draft-notes">docs.gitlab</a>
     * @param projectId
     * @param mergeRequestIid
     */
    void publishAllPendingDraftNotes(long projectId, long mergeRequestIid) throws IOException;

    Project getProject(String projectSlug) throws IOException;


}
