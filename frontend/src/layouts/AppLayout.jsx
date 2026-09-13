import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Sidebar from "../components/Sidebar";
import Avatar from "../components/Avatar";
import { Bell, Search } from "lucide-react";
import { useAuth } from "../context/AuthContext";

export default function AppLayout({ children, title }) {
  const { user } = useAuth();
  const [search, setSearch] = useState("");
  const navigate = useNavigate();

  const handleSearch = (e) => {
    e.preventDefault();
    if (!search.trim()) return;
    // Global search sends the user to Question Bank with the query in the URL,
    // where it's actually applied (see QuestionBank.jsx) — not decorative.
    navigate(`/questions?q=${encodeURIComponent(search.trim())}`);
  };

  return (
    <div className="flex bg-slate-50 min-h-screen">
      <Sidebar />

      <div className="ml-64 flex-1">
        <header className="h-16 bg-white border-b border-slate-200 flex items-center justify-between px-6 sticky top-0 z-10">
          <h1 className="text-lg font-semibold text-slate-900">{title}</h1>

          <div className="flex items-center gap-4">
            <form onSubmit={handleSearch} className="relative">
              <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
              <input
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                placeholder="Search questions..."
                className="pl-9 pr-3 py-1.5 text-sm border border-slate-200 rounded-lg w-56 focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </form>
            <Bell className="w-5 h-5 text-slate-400" />
            <Avatar name={user?.fullName || "?"} size="sm" />
          </div>
        </header>

        <main className="p-6">{children}</main>
      </div>
    </div>
  );
}
