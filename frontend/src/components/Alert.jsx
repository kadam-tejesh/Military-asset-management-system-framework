// Alert.jsx
export default function Alert({ msg }) {
  if (!msg) return null;
  return <div className={`alert ${msg.type}`}>{msg.text}</div>;
}