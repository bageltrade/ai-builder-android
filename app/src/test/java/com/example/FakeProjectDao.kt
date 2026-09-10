package com.example

import com.example.data.local.ProjectDao
import com.example.data.local.ProjectEntity
import com.example.data.local.ProjectVersionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeProjectDao : ProjectDao {
    private val projectsMap = mutableMapOf<String, ProjectEntity>()
    private val projectsFlow = MutableStateFlow<List<ProjectEntity>>(emptyList())
    private val versionsMap = mutableMapOf<String, MutableList<ProjectVersionEntity>>()
    private val versionsFlowMap = mutableMapOf<String, MutableStateFlow<List<ProjectVersionEntity>>>()

    override fun getAllProjects(): Flow<List<ProjectEntity>> = projectsFlow

    override fun getProjectById(id: String): Flow<ProjectEntity?> {
        return projectsFlow.map { list -> list.find { it.id == id } }
    }

    override suspend fun getProjectDirect(id: String): ProjectEntity? {
        return projectsMap[id]
    }

    override suspend fun insertOrUpdateProject(project: ProjectEntity) {
        projectsMap[project.id] = project
        projectsFlow.value = projectsMap.values.sortedByDescending { it.updatedAt }
    }

    override suspend fun updateProject(project: ProjectEntity) {
        insertOrUpdateProject(project)
    }

    override suspend fun deleteProject(id: String) {
        projectsMap.remove(id)
        projectsFlow.value = projectsMap.values.sortedByDescending { it.updatedAt }
    }

    override suspend fun insertVersion(version: ProjectVersionEntity) {
        val list = versionsMap.getOrPut(version.projectId) { mutableListOf() }
        list.add(version)
        val flow = versionsFlowMap.getOrPut(version.projectId) { MutableStateFlow(emptyList()) }
        flow.value = list.sortedByDescending { it.versionNumber }
    }

    override fun getVersionsForProject(projectId: String): Flow<List<ProjectVersionEntity>> {
        return versionsFlowMap.getOrPut(projectId) { MutableStateFlow(emptyList()) }
    }

    override suspend fun getVersion(projectId: String, version: Int): ProjectVersionEntity? {
        return versionsMap[projectId]?.find { it.versionNumber == version }
    }
}
