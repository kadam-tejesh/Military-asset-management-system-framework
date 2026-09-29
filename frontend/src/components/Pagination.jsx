export default function Pagination({ page, totalPages, onPage }) {
  if (totalPages <= 1) return null;
  return (
    <div className="pager">
      <button className="btn ghost" disabled={page === 0} onClick={() => onPage(page - 1)}>‹ Prev</button>
      <span>Page {page + 1} of {totalPages}</span>
      <button className="btn ghost" disabled={page >= totalPages - 1} onClick={() => onPage(page + 1)}>Next ›</button>
    </div>
  );
}