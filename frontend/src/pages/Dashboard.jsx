import { useEffect, useState } from "react";
import api, { clean, errMsg, fmtDate, fmtDateTime } from "../api/api.js";
import { useAuth } from "../context/AuthContext.jsx";
import useLookups from "../hooks/useLookups.js";
import FilterBar from "../components/FilterBar.jsx";
import DataTable from "../components/DataTable.jsx";
import Modal from "../components/Modal.jsx";
import Alert from "../components/Alert.jsx";

const INITIAL = { baseId: "", equipmentTypeId: "", from: "", to: "" };

function Metric({ label, value, sub, onClick, accent }) {
  return (
    <div className={`metric card ${onClick ? "clickable" : ""} ${accent ? "accent" : ""}`}
         onClick={onClick} role={onClick ? "button" : undefined}>
      <span className="metric-label">{label}</span>
      <span className="metric-value">{value}</span>
      {sub && <span className="metric-sub">{sub}</span>}
    </div>
  );
}

export default function Dashboard() {
  const { user } = useAuth();
  const { bases, types } = useLookups();
  const [filters, setFilters] = useState(INITIAL);
  const [data, setData] = useState(null);
  const [msg, setMsg] = useState(null);
  const [showNet, setShowNet] = useState(false);
  const [net, setNet] = useState(null);
  const [tab, setTab] = useState("purchases");

  useEffect(() => {
    let live = true;
    api.get("/dashboard", { params: clean(filters) })
      .then((r) => { if (live) { setData(r.data); setMsg(null); } })
      .catch((e) => live && setMsg({ type: "error", text: errMsg(e) }));
    return () => { live = false; };
  }, [filters]);

  const openNet = async () => {
    setShowNet(true);
    setNet(null);
    setTab("purchases");
    try {
      const { data: d } = await api.get("/dashboard/net-movement", { params: clean(filters) });
      setNet(d);
    } catch (e) {
      setMsg({ type: "error", text: errMsg(e) });
    }
  };

  const purchaseCols = [
    { key: "purchaseDate", label: "Date", render: (r) => fmtDate(r.purchaseDate) },
    { key: "baseName", label: "Base" },
    { key: "equipmentType", label: "Equipment" },
    { key: "quantity", label: "Qty" },
    { key: "vendor", label: "Vendor", render: (r) => r.vendor || "—" },
  ];
  const transferCols = [
    { key: "transferDate", label: "Date", render: (r) => fmtDateTime(r.transferDate) },
    { key: "equipmentType", label: "Equipment" },
    { key: "quantity", label: "Qty" },
    { key: "sourceBase", label: "From" },
    { key: "destinationBase", label: "To" },
  ];

  const tabs = net && [
    { id: "purchases", label: `Purchases (${net.purchases.length})`, cols: purchaseCols, rows: net.purchases },
    { id: "in", label: `Transfer In (${net.transfersIn.length})`, cols: transferCols, rows: net.transfersIn },
    { id: "out", label: `Transfer Out (${net.transfersOut.length})`, cols: transferCols, rows: net.transfersOut },
  ];
  const active = tabs?.find((t) => t.id === tab);

  return (
    <>
      <h2>Dashboard</h2>
      <FilterBar filters={filters} onChange={setFilters} initial={INITIAL}
                 bases={bases} types={types} showBase={user.role === "ADMIN"} />
      <p className="muted small">Leave the dates empty to see the current month.</p>
      <Alert msg={msg} />

      {!data ? <p className="muted">Loading...</p> : (
        <div className="metrics">
          <Metric label="Opening Balance" value={data.openingBalance} />
          <Metric label="Closing Balance" value={data.closingBalance} accent
                  sub="Opening + Net Movement − Expended" />
          <Metric label="Net Movement" value={data.netMovement} onClick={openNet}
                  sub={`+${data.purchases} purchases · +${data.transfersIn} in · −${data.transfersOut} out`} />
          <Metric label="Assigned" value={data.assigned} />
          <Metric label="Expended" value={data.expended} />
        </div>
      )}

      {showNet && (
        <Modal title="Net Movement details" onClose={() => setShowNet(false)}>
          {!net ? <p className="muted">Loading...</p> : (
            <>
              <div className="tabs">
                {tabs.map((t) => (
                  <button key={t.id} className={t.id === tab ? "tab active" : "tab"} onClick={() => setTab(t.id)}>
                    {t.label}
                  </button>
                ))}
              </div>
              <DataTable columns={active.cols} rows={active.rows} />
            </>
          )}
        </Modal>
      )}
    </>
  );
}