package ru.practicum.manager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected void initializeManager() {
        taskManager = Managers.getDefault();
    }

}