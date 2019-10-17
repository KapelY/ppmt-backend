package io.igileintelligence.ppmtool.services;

import io.igileintelligence.ppmtool.domain.Backlog;
import io.igileintelligence.ppmtool.domain.ProjectTask;
import io.igileintelligence.ppmtool.exceptions.ProjectNotFoundException;
import io.igileintelligence.ppmtool.repositories.BacklogRepository;
import io.igileintelligence.ppmtool.repositories.ProjectTaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ProjectTaskService {
    private BacklogRepository backlogRepository;
    private ProjectTaskRepository projectTaskRepository;
    private ProjectService projectService;

    public ProjectTaskService(BacklogRepository backlogRepository, ProjectTaskRepository projectTaskRepository,
                              ProjectService projectService) {
        this.backlogRepository = backlogRepository;
        this.projectTaskRepository = projectTaskRepository;
        this.projectService = projectService;
    }

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask, String username) {
        Backlog backlog = projectService.findProjectByIdentifier(projectIdentifier, username).getBacklog();
        if (backlog == null) {
            throw new ProjectNotFoundException("Project by Identifier not found");
        }
        projectTask.setBacklog(backlog);
        Integer backlogSequence = backlog.getPTSequence();
        backlogSequence++;
        backlog.setPTSequence(backlogSequence);
        projectTask.setProjectSequence(projectIdentifier + "-" + backlogSequence);
        projectTask.setProjectIdentifier(projectIdentifier);

        if (Objects.equals(projectTask.getStatus(), "") || projectTask.getStatus() == null) {
            projectTask.setStatus("TO_DO");
        }

        if (projectTask.getPriority() == null || projectTask.getPriority() == 0) {
            projectTask.setPriority(3);
        }
        return projectTaskRepository.save(projectTask);
    }

    public List<ProjectTask> findBacklogByIdentifier(String backlog_identifier, String username) {
        projectService.findProjectByIdentifier(backlog_identifier, username);
        return projectTaskRepository.findByProjectIdentifierOrderByPriority(backlog_identifier);
    }

    public ProjectTask findPTByProjectSequence(String backlog_id, String pt_id, String username) {
        projectService.findProjectByIdentifier(backlog_id, username);

        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);

        if (projectTask == null) {
            throw new ProjectNotFoundException("Project Task '" + pt_id + "' not found");
        }

        if (!projectTask.getProjectIdentifier().equals(backlog_id)) {
            throw new ProjectNotFoundException("Project Task '" + pt_id + "' does not exist in project: '" +
                    backlog_id + "'");
        }

        return projectTask;
    }

    public ProjectTask updateByProjectSequence(ProjectTask projectTaskToUpdate, String backlog_id,
                                               String pt_id, String username) {
        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id, username);
        projectTask = projectTaskToUpdate;

        return projectTaskRepository.save(projectTask);
    }

    public void deletePTByProjectSequence(String backlog_id, String pt_id, String username) {
        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id, username);
        projectTaskRepository.delete(projectTask);
    }
}
