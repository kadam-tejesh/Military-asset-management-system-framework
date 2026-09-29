import { useEffect, useState } from "react";
import api, { errMsg, fmtDateTime } from "../api/api.js";
import DataTable from "../components/DataTable.jsx";
import Pagination from "../components/Pagination.jsx";
import Alert from "../components/Alert.jsx";

export default function AuditLogs() {
  const [page, setPage] = useState(0);
  const [data, setData] = useState({ content: [], totalPages: 0 });
  const [msg, setMsg] = useState(null);

  useEffect(() => {
    api.get("/audit-logs", { params: { page, size: 15 } })
      .then((r) => setData(r.data))
      .catch((e) => setMsg({ type: "error", text: errMsg(e) }));
  }, [page]);

  const columns = [
    { key: "timestamp", label: "Time", render: (r) => fmtDateTime(r.timestamp) },
    { key: "username", label: "User" },
    { key: "action", label: "Action" },
    { key: "entityType", label: "Entity" },
    { key: "entityId", label: "ID" },
    { key: "ipAddress", label: "IP" },
  ];

  return (
    <>
      <h2>Audit Log</h2>
      <Alert msg={msg} />
      <DataTable columns={columns} rows={data.content} />
      <Pagination page={page} totalPages={data.totalPages} onPage={setPage} />
    </>
  );
}