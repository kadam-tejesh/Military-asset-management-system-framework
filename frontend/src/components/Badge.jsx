// Badge.jsx
export default function Badge({ value }) {
  return <span className={`badge s-${String(value).toLowerCase()}`}>{value}</span>;
}