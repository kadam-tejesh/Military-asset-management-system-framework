import { useState } from "react";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";

const ALL = ["ADMIN", "BASE_COMMANDER", "LOGISTICS_OFFICER"];
const NAV = [
  { to: "/", label: "Dashboard", roles: ALL },
  { to: "/purchases", label: "Purchases", roles: ALL },
  { to: "/transfers", label: "Transfers", roles: ALL },
  { to: "/assignments", label: "Assignments", roles: ["ADMIN", "BASE_COMMANDER"] },
  { to: "/audit", label: "Audit Log", roles: ["ADMIN"] },
];

export default function Layout() {
  const { user, logout } = useAuth();
  const [open, setOpen] = useState(false);
  const navigate = useNavigate();

  return (
    <div className="app">
      <header className="topbar">
        <div className="brand">★ MAMS</div>
        <button className="burger" onClick={() => setOpen(!open)} aria-label="Toggle menu">☰</button>
        <nav className={open ? "nav open" : "nav"}>
          {NAV.filter((l) => l.roles.includes(user.role)).map((l) => (
            <NavLink key={l.to} to={l.to} end={l.to === "/"} onClick={() => setOpen(false)}>
              {l.label}
            </NavLink>
          ))}
          <div className="user">
            <span>{user.username}</span>
            <span className="badge">{user.role.replaceAll("_", " ")}</span>
            {user.baseName && <span className="muted">{user.baseName}</span>}
            <button className="btn ghost" onClick={() => { logout(); navigate("/login"); }}>
              Logout
            </button>
          </div>
        </nav>
      </header>
      <main className="content"><Outlet /></main>
    </div>
  );
}