export default function FilterBar({ filters, onChange, initial, bases, types, showBase, children }) {
  const set = (key, value) => onChange({ ...filters, [key]: value });

  return (
    <div className="filters card">
      {showBase && (
        <label>Base
          <select value={filters.baseId} onChange={(e) => set("baseId", e.target.value)}>
            <option value="">All bases</option>
            {bases.map((b) => <option key={b.id} value={b.id}>{b.name}</option>)}
          </select>
        </label>
      )}
      <label>Equipment type
        <select value={filters.equipmentTypeId} onChange={(e) => set("equipmentTypeId", e.target.value)}>
          <option value="">All types</option>
          {types.map((t) => <option key={t.id} value={t.id}>{t.name}</option>)}
        </select>
      </label>
      <label>From
        <input type="date" value={filters.from} onChange={(e) => set("from", e.target.value)} />
      </label>
      <label>To
        <input type="date" value={filters.to} onChange={(e) => set("to", e.target.value)} />
      </label>
      {children}
      <button className="btn ghost" onClick={() => onChange(initial)}>Clear</button>
    </div>
  );
}