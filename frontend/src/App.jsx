import { Routes, Route, Navigate } from "react-router-dom";
import Layout from "./components/Layout.jsx";
import ProtectedRoute from "./components/ProtectedRoute.jsx";
import Login from "./pages/Login.jsx";
import Dashboard from "./pages/Dashboard.jsx";
import Purchases from "./pages/Purchases.jsx";
import Transfers from "./pages/Transfers.jsx";
import Assignments from "./pages/Assignments.jsx";
import AuditLogs from "./pages/AuditLogs.jsx";

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route element={<ProtectedRoute><Layout /></ProtectedRoute>}>
        <Route index element={<Dashboard />} />
        <Route path="purchases" element={<Purchases />} />
        <Route path="transfers" element={<Transfers />} />
        <Route
          path="assignments"
          element={<ProtectedRoute roles={["ADMIN", "BASE_COMMANDER"]}><Assignments /></ProtectedRoute>}
        />
        <Route
          path="audit"
          element={<ProtectedRoute roles={["ADMIN"]}><AuditLogs /></ProtectedRoute>}
        />
        <Route
          path="unauthorized"
          element={<div className="card"><h2>Access denied</h2><p>Your role cannot open this page.</p></div>}
        />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
