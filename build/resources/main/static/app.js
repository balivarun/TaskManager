const state = {
  token: localStorage.getItem("token"),
  user: null,
  users: [],
  projects: [],
  tasks: []
};

const authPanel = document.getElementById("auth-panel");
const dashboardPanel = document.getElementById("dashboard-panel");
const userMeta = document.getElementById("user-meta");
const statsGrid = document.getElementById("stats-grid");
const projectsList = document.getElementById("projects-list");
const tasksList = document.getElementById("tasks-list");
const taskProjectSelect = document.getElementById("task-project-select");
const taskAssigneeSelect = document.getElementById("task-assignee-select");

document.getElementById("login-form").addEventListener("submit", handleLogin);
document.getElementById("signup-form").addEventListener("submit", handleSignup);
document.getElementById("project-form").addEventListener("submit", handleProjectSubmit);
document.getElementById("task-form").addEventListener("submit", handleTaskSubmit);
document.getElementById("logout-btn").addEventListener("click", logout);
taskProjectSelect.addEventListener("change", populateAssigneeOptions);

boot();

async function boot() {
  if (!state.token) {
    renderAuth();
    return;
  }

  try {
    state.user = await api("/api/auth/me");
    await loadDashboard();
    renderApp();
  } catch (error) {
    logout();
  }
}

async function handleLogin(event) {
  event.preventDefault();
  const formData = new FormData(event.target);
  const response = await api("/api/auth/login", "POST", Object.fromEntries(formData.entries()), false);
  setSession(response);
}

async function handleSignup(event) {
  event.preventDefault();
  const formData = new FormData(event.target);
  const response = await api("/api/auth/signup", "POST", Object.fromEntries(formData.entries()), false);
  setSession(response);
}

async function handleProjectSubmit(event) {
  event.preventDefault();
  const formData = new FormData(event.target);
  await api("/api/projects", "POST", normalizePayload(formData));
  event.target.reset();
  await loadDashboard();
  renderApp();
  flash("Project saved");
}

async function handleTaskSubmit(event) {
  event.preventDefault();
  const formData = new FormData(event.target);
  await api("/api/tasks", "POST", normalizePayload(formData));
  event.target.reset();
  populateProjectOptions();
  await loadDashboard();
  renderApp();
  flash("Task saved");
}

function setSession(response) {
  state.token = response.token;
  state.user = {
    userId: response.userId,
    fullName: response.fullName,
    email: response.email,
    role: response.role
  };
  localStorage.setItem("token", state.token);
  loadDashboard().then(renderApp);
}

function logout() {
  localStorage.removeItem("token");
  state.token = null;
  state.user = null;
  renderAuth();
}

async function loadDashboard() {
  const dashboard = await api("/api/dashboard");
  const projects = await api("/api/projects");
  const tasks = await api("/api/tasks");
  const users = state.user.role === "ADMIN" ? await api("/api/users") : [];
  state.dashboard = dashboard;
  state.users = users;
  state.projects = projects;
  state.tasks = tasks;
}

function renderAuth() {
  authPanel.classList.remove("hidden");
  dashboardPanel.classList.add("hidden");
}

function renderApp() {
  authPanel.classList.add("hidden");
  dashboardPanel.classList.remove("hidden");
  userMeta.textContent = `${state.user.fullName} | ${state.user.role} | ${state.user.email}`;
  document.querySelectorAll(".admin-only").forEach((el) => {
    el.classList.toggle("hidden", state.user.role !== "ADMIN");
  });
  renderStats();
  renderProjects();
  renderTasks();
  populateProjectOptions();
}

function renderStats() {
  const cards = [
    { label: "Projects", value: state.dashboard.totalProjects },
    { label: "Tasks", value: state.dashboard.totalTasks },
    { label: "Overdue", value: state.dashboard.overdueTasks },
    { label: "Done", value: state.dashboard.taskCountsByStatus.DONE || 0 }
  ];
  statsGrid.innerHTML = cards.map(card => `
    <article class="stat-card">
      <div class="muted">${card.label}</div>
      <h2>${card.value}</h2>
    </article>
  `).join("");
}

function renderProjects() {
  projectsList.innerHTML = state.projects.map(project => `
    <article class="project-card">
      <div class="project-card-head">
        <div>
          <h3>${project.name}</h3>
          <div class="muted">Owner: ${project.owner.fullName}</div>
        </div>
        <div class="muted">${project.completedTasks}/${project.totalTasks} done</div>
      </div>
      <p>${project.description || "No description"}</p>
      <div class="project-members">
        <div class="muted">Due: ${project.dueDate || "-"}</div>
        <div class="muted">Team: ${project.members.map(member => member.fullName).join(", ")}</div>
      </div>
      ${state.user.role === "ADMIN" ? `
      <div class="inline-actions">
        <select data-project-member="${project.id}">
          <option value="">Add team member</option>
          ${state.users
            .filter(user => !project.members.some(member => member.id === user.id))
            .map(user => `<option value="${user.id}">${user.fullName} (${user.role})</option>`)
            .join("")}
        </select>
        <button type="button" onclick="addMember(${project.id}, this.previousElementSibling.value)">Add</button>
      </div>` : ""}
    </article>
  `).join("");
}

function renderTasks() {
  tasksList.innerHTML = state.tasks.map(task => `
    <article class="task-card">
      <div class="task-card-head">
        <div>
          <h3>${task.title}</h3>
          <div class="muted">${task.projectName}</div>
        </div>
        <div class="task-meta">
          <span class="badge ${task.status.toLowerCase()}">${task.status.replace("_", " ")}</span>
          <span class="badge ${task.priority.toLowerCase()}">${task.priority}</span>
        </div>
      </div>
      <p>${task.description || "No description"}</p>
      <div class="task-meta">
        <span class="muted">Assignee: ${task.assignee ? task.assignee.fullName : "Unassigned"}</span>
        <span class="muted">Due: ${task.dueDate || "-"}</span>
      </div>
      ${(state.user.role === "ADMIN" || (task.assignee && task.assignee.id === state.user.userId)) ? `
      <div class="inline-actions">
        <select data-task-status="${task.id}">
          ${["TODO", "IN_PROGRESS", "DONE"].map(status => `
            <option value="${status}" ${task.status === status ? "selected" : ""}>${status.replace("_", " ")}</option>
          `).join("")}
        </select>
        <button type="button" onclick="updateTaskStatus(${task.id}, this.previousElementSibling.value)">Update Status</button>
      </div>` : ""}
    </article>
  `).join("");
}

function populateProjectOptions() {
  const projectOptions = state.projects.map(project => `<option value="${project.id}">${project.name}</option>`).join("");
  taskProjectSelect.innerHTML = projectOptions;
  populateAssigneeOptions();
}

function populateAssigneeOptions() {
  const projectId = Number(taskProjectSelect.value);
  const project = state.projects.find(item => item.id === projectId);
  const members = project ? project.members : [];
  taskAssigneeSelect.innerHTML = `<option value="">Unassigned</option>` + members
    .map(member => `<option value="${member.id}">${member.fullName}</option>`)
    .join("");
}

async function addMember(projectId, userId) {
  if (!userId) {
    flash("Choose a user first");
    return;
  }
  await api(`/api/projects/${projectId}/members/${userId}`, "POST");
  await loadDashboard();
  renderApp();
  flash("Member added");
}

async function updateTaskStatus(taskId, status) {
  await api(`/api/tasks/${taskId}/status`, "PATCH", { status });
  await loadDashboard();
  renderApp();
  flash("Status updated");
}

async function api(url, method = "GET", body, useAuth = true) {
  const headers = { "Content-Type": "application/json" };
  if (useAuth && state.token) {
    headers.Authorization = `Bearer ${state.token}`;
  }
  const response = await fetch(url, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined
  });
  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: "Request failed" }));
    flash(error.message || "Request failed");
    throw new Error(error.message || "Request failed");
  }
  return response.status === 204 ? null : response.json();
}

function normalizePayload(formData) {
  const data = Object.fromEntries(formData.entries());
  Object.keys(data).forEach((key) => {
    if (data[key] === "") {
      data[key] = null;
    }
  });
  if (data.projectId) {
    data.projectId = Number(data.projectId);
  }
  if (data.assigneeId) {
    data.assigneeId = Number(data.assigneeId);
  }
  return data;
}

function flash(text) {
  const existing = document.querySelector(".message");
  if (existing) {
    existing.remove();
  }
  const el = document.createElement("div");
  el.className = "message";
  el.textContent = text;
  document.body.appendChild(el);
  setTimeout(() => el.remove(), 2400);
}

window.addMember = addMember;
window.updateTaskStatus = updateTaskStatus;
