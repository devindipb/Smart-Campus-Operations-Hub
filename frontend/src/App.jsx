import { useEffect, useState } from "react";
import { createResource, deleteResource, fetchCurrentUser, fetchResources, updateResource } from "./api";

const initialForm = {
  resourceCode: "",
  name: "",
  type: "LECTURE_HALL",
  capacity: 0,
  location: "",
  description: "",
  availableFrom: "08:00",
  availableTo: "17:00",
  status: "ACTIVE",
  active: true
};

const defaultFilters = {
  keyword: "",
  type: "",
  minCapacity: "",
  location: "",
  status: "",
  active: ""
};

export default function App() {
  const [credentials, setCredentials] = useState({
    username: "admin@sliit.lk",
    password: "Admin@123"
  });
  const [filters, setFilters] = useState(defaultFilters);
  const [resources, setResources] = useState([]);
  const [form, setForm] = useState(initialForm);
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [currentUser, setCurrentUser] = useState(null);

  useEffect(() => {
    initializeSession();
  }, []);

  async function initializeSession() {
    try {
      setLoading(true);
      setError("");
      const user = await fetchCurrentUser(credentials);
      setCurrentUser(user);
      const data = await fetchResources(filters, credentials);
      setResources(data);
    } catch (err) {
      setCurrentUser(null);
      setResources([]);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  async function loadResources(currentFilters = filters) {
    try {
      setLoading(true);
      setError("");
      const user = await fetchCurrentUser(credentials);
      setCurrentUser(user);
      const data = await fetchResources(currentFilters, credentials);
      setResources(data);
    } catch (err) {
      setCurrentUser(null);
      setResources([]);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  function handleFilterChange(event) {
    const { name, value } = event.target;
    setFilters((previous) => ({ ...previous, [name]: value }));
  }

  function handleFormChange(event) {
    const { name, value, type, checked } = event.target;
    setForm((previous) => ({
      ...previous,
      [name]: type === "checkbox" ? checked : value
    }));
  }

  async function handleSearch(event) {
    event.preventDefault();
    await loadResources(filters);
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");

    const payload = {
      ...form,
      capacity: Number(form.capacity),
      active: Boolean(form.active)
    };

    try {
      if (editingId) {
        await updateResource(editingId, payload, credentials);
      } else {
        await createResource(payload, credentials);
      }
      resetForm();
      await loadResources();
    } catch (err) {
      setError(err.message);  
    }
  }

  function handleEdit(resource) {
    setEditingId(resource.id);
    setForm({
      resourceCode: resource.resourceCode,
      name: resource.name,
      type: resource.type,
      capacity: resource.capacity,
      location: resource.location,
      description: resource.description || "",
      availableFrom: resource.availableFrom,
      availableTo: resource.availableTo,
      status: resource.status,
      active: resource.active
    });
  }

  async function handleDelete(id) {
    try {
      await deleteResource(id, credentials);
      await loadResources();
    } catch (err) {
      setError(err.message);
    }
  }

  function resetForm() {
    setEditingId(null);
    setForm(initialForm);
  }

  const isAdmin = currentUser?.roles?.includes("ROLE_ADMIN");
  const activeCount = resources.filter((resource) => resource.status === "ACTIVE").length;
  const outOfServiceCount = resources.filter((resource) => resource.status === "OUT_OF_SERVICE").length;
  const equipmentCount = resources.filter((resource) => resource.type === "EQUIPMENT").length;

  return (
    <div className="page-shell">
      <div className="page-orbit page-orbit-one" />
      <div className="page-orbit page-orbit-two" />

      <header className="hero">
        <div className="hero-copy-block">
          <p className="eyebrow">IT3030 PAF 2026</p>
          <h1>Facilities & Assets Catalogue</h1>
          <p className="hero-copy">
            Module A dashboard for managing lecture halls, labs, meeting rooms, and equipment.
          </p>
          <div className="hero-badges">
            <span className="hero-badge">Smart Campus</span>
            <span className="hero-badge">Live Catalogue</span>
            <span className="hero-badge">Role Aware</span>
          </div>
        </div>

        <div className="auth-card">
          <h2>Demo Login</h2>
          <p className="helper-text">
            Sign in as <strong>ADMIN</strong> to manage resources or as <strong>USER</strong> to browse the catalogue.
          </p>
          <label>
            Username
            <input
              name="username"
              value={credentials.username}
              onChange={(e) => setCredentials((prev) => ({ ...prev, username: e.target.value }))}
            />
          </label>
          <label>
            Password
            <input
              name="password"
              type="password"
              value={credentials.password}
              onChange={(e) => setCredentials((prev) => ({ ...prev, password: e.target.value }))}
            />
          </label>
          <button className="secondary-button" onClick={() => loadResources()}>
            Refresh With Credentials
          </button>
          {currentUser ? (
            <p className="session-note">
              Signed in as <strong>{currentUser.username}</strong>
            </p>
          ) : null}
        </div>
      </header>

      <section className="stats-strip">
        <article className="stat-card">
          <span className="stat-label">Total Resources</span>
          <strong className="stat-value">{resources.length}</strong>
          <span className="stat-note">Visible in current result set</span>
        </article>
        <article className="stat-card">
          <span className="stat-label">Active</span>
          <strong className="stat-value">{activeCount}</strong>
          <span className="stat-note">Ready for booking workflows</span>
        </article>
        <article className="stat-card">
          <span className="stat-label">Out of Service</span>
          <strong className="stat-value">{outOfServiceCount}</strong>
          <span className="stat-note">Need maintenance attention</span>
        </article>
        <article className="stat-card">
          <span className="stat-label">Equipment Items</span>
          <strong className="stat-value">{equipmentCount}</strong>
          <span className="stat-note">Portable shared assets</span>
        </article>
      </section>

      {error ? <div className="error-banner">{error}</div> : null}

      <section className="grid-layout">
        <form className="card feature-card" onSubmit={handleSearch}>
          <h2>Search & Filter</h2>
          <p className="section-intro">
            Narrow the catalogue by resource type, campus location, capacity, lifecycle status, or keyword.
          </p>
          <div className="form-grid">
            <label>
              Keyword
              <input name="keyword" value={filters.keyword} onChange={handleFilterChange} />
            </label>
            <label>
              Type
              <select name="type" value={filters.type} onChange={handleFilterChange}>
                <option value="">All</option>
                <option value="LECTURE_HALL">Lecture Hall</option>
                <option value="LAB">Lab</option>
                <option value="MEETING_ROOM">Meeting Room</option>
                <option value="EQUIPMENT">Equipment</option>
              </select>
            </label>
            <label>
              Min Capacity
              <input name="minCapacity" type="number" value={filters.minCapacity} onChange={handleFilterChange} />
            </label>
            <label>
              Location
              <input name="location" value={filters.location} onChange={handleFilterChange} />
            </label>
            <label>
              Status
              <select name="status" value={filters.status} onChange={handleFilterChange}>
                <option value="">All</option>
                <option value="ACTIVE">Active</option>
                <option value="OUT_OF_SERVICE">Out of Service</option>
              </select>
            </label>
            <label>
              Active Flag
              <select name="active" value={filters.active} onChange={handleFilterChange}>
                <option value="">All</option>
                <option value="true">True</option>
                <option value="false">False</option>
              </select>
            </label>
          </div>
          <div className="actions">
            <button type="submit">Apply Filters</button>
            <button type="button" className="secondary-button" onClick={() => {
              setFilters(defaultFilters);
              loadResources(defaultFilters);
            }}>
              Reset
            </button>
          </div>
        </form>

        <section className="card feature-card">
          <h2>{editingId ? "Edit Resource" : "Add Resource"}</h2>
          <p className="section-intro">
            Maintain structured catalogue entries with clean metadata for future booking and maintenance modules.
          </p>
          {isAdmin ? (
            <form onSubmit={handleSubmit}>
              <div className="form-grid">
                <label>
                  Resource Code
                  <input name="resourceCode" value={form.resourceCode} onChange={handleFormChange} required />
                </label>
                <label>
                  Name
                  <input name="name" value={form.name} onChange={handleFormChange} required />
                </label>
                <label>
                  Type
                  <select name="type" value={form.type} onChange={handleFormChange}>
                    <option value="LECTURE_HALL">Lecture Hall</option>
                    <option value="LAB">Lab</option>
                    <option value="MEETING_ROOM">Meeting Room</option>
                    <option value="EQUIPMENT">Equipment</option>
                  </select>
                </label>
                <label>
                  Capacity
                  <input name="capacity" type="number" min="0" value={form.capacity} onChange={handleFormChange} required />
                </label>
                <label>
                  Location
                  <input name="location" value={form.location} onChange={handleFormChange} required />
                </label>
                <label>
                  Status
                  <select name="status" value={form.status} onChange={handleFormChange}>
                    <option value="ACTIVE">Active</option>
                    <option value="OUT_OF_SERVICE">Out of Service</option>
                  </select>
                </label>
                <label>
                  Available From
                  <input name="availableFrom" type="time" value={form.availableFrom} onChange={handleFormChange} required />
                </label>
                <label>
                  Available To
                  <input name="availableTo" type="time" value={form.availableTo} onChange={handleFormChange} required />
                </label>
                <label className="full-width">
                  Description
                  <textarea name="description" rows="3" value={form.description} onChange={handleFormChange} />
                </label>
                <label className="checkbox-row">
                  <input name="active" type="checkbox" checked={form.active} onChange={handleFormChange} />
                  Active Resource
                </label>
              </div>
              <div className="actions">
                <button type="submit">{editingId ? "Update Resource" : "Create Resource"}</button>
                <button type="button" className="secondary-button" onClick={resetForm}>
                  Clear
                </button>
              </div>
            </form>
          ) : (
            <p className="helper-text">
              Resource creation, editing, and deletion are available only for admin accounts.
            </p>
          )}
        </section>
      </section>

      <section className="card table-card">
        <div className="table-header">
          <div>
            <h2>Catalogue Items</h2>
            <p className="section-intro table-intro">
              Review the current directory of bookable spaces and shared assets.
            </p>
          </div>
          <span className="table-count">{loading ? "Loading..." : `${resources.length} resource(s)`}</span>
        </div>

        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Code</th>
                <th>Name</th>
                <th>Type</th>
                <th>Capacity</th>
                <th>Location</th>
                <th>Window</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {resources.map((resource) => (
                <tr key={resource.id}>
                  <td>{resource.resourceCode}</td>
                  <td>{resource.name}</td>
                  <td>{resource.type.replaceAll("_", " ")}</td>
                  <td>{resource.capacity}</td>
                  <td>{resource.location}</td>
                  <td>{resource.availableFrom} - {resource.availableTo}</td>
                  <td>
                    <span className={`status-pill ${resource.status === "ACTIVE" ? "green" : "amber"}`}>
                      {resource.status}
                    </span>
                  </td>
                  <td className="action-row">
                    {isAdmin ? (
                      <>
                        <button type="button" className="secondary-button" onClick={() => handleEdit(resource)}>
                          Edit
                        </button>
                        <button type="button" className="danger-button" onClick={() => handleDelete(resource.id)}>
                          Delete
                        </button>
                      </>
                    ) : (
                      <span className="muted-text">View Only</span>
                    )}
                  </td>
                </tr>
              ))}
              {!loading && resources.length === 0 ? (
                <tr>
                  <td colSpan="8" className="empty-state">
                    No resources matched the selected filters.
                  </td>
                </tr>
              ) : null}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
}
