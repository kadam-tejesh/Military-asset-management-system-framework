import { useEffect, useState } from "react";
import api from "../api/api.js";

/** bases: what this user may filter by. allBases: every base (transfer destinations). */
export default function useLookups() {
  const [bases, setBases] = useState([]);
  const [allBases, setAllBases] = useState([]);
  const [types, setTypes] = useState([]);

  useEffect(() => {
    Promise.all([
      api.get("/lookup/bases"),
      api.get("/lookup/all-bases"),
      api.get("/lookup/equipment-types"),
    ])
      .then(([b, ab, t]) => {
        setBases(b.data);
        setAllBases(ab.data);
        setTypes(t.data);
      })
      .catch(() => {});
  }, []);

  return { bases, allBases, types };
}