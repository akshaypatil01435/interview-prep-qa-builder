import { NavLink } from "react-router-dom";
import {
  LayoutDashboard,
  BookOpen,
  Bookmark,
  FileQuestion,
  Mic,
  BarChart2,
  History,
  StickyNote,
  Settings,
  LogOut,
  Shield,
  Layers,
  Users,
} from "lucide-react";

import { useAuth } from "../context/AuthContext";

const navSections = [
  {
    title: "Overview",
    links: [{ to: "/dashboard", label: "Dashboard", icon: LayoutDashboard }],
  },
  {
    title: "Practice",
    links: [
      { to: "/questions", label: "Question Bank", icon: BookOpen },
      { to: "/bookmarks", label: "Bookmarks", icon: Bookmark },
      { to: "/quiz", label: "Quiz", icon: FileQuestion },
      { to: "/mock-interview", label: "Mock Interview", icon: Mic },
    ],
  },
  {
    title: "Tracking",
    links: [
      { to: "/progress", label: "Progress", icon: BarChart2 },
      { to: "/history", label: "Practice History", icon: History },
    ],
  },
  {
    title: "Personal",
    links: [
      { to: "/notes", label: "My Notes", icon: StickyNote },
      { to: "/profile", label: "Profile", icon: Settings },
    ],
  },
];

const adminLinks = [
  { to: "/admin", label: "Admin Dashboard", icon: Shield },
  { to: "/admin/questions", label: "Manage Questions", icon: FileQuestion },
  { to: "/admin/topics", label: "Manage Topics", icon: Layers },
  { to: "/admin/users", label: "Users", icon: Users },
];

function NavItem({ to, label, icon: Icon }) {
  return (
    <NavLink
      to={to}
      className={({ isActive }) =>
        `flex items-center gap-3 px-3 py-2 rounded-lg text-sm mb-1 ${
          isActive ? "bg-blue-600 text-white" : "hover:bg-slate-800"
        }`
      }
    >
      <Icon className="w-4 h-4" />
      {label}
    </NavLink>
  );
}

export default function Sidebar() {
  const { logout, isAdmin } = useAuth();

  return (
    <aside className="w-64 bg-slate-900 text-slate-300 flex flex-col h-screen fixed left-0 top-0">
      <div className="px-5 py-5 text-white font-semibold text-lg border-b border-slate-800">
        Interview Prep
      </div>

      <nav className="flex-1 overflow-y-auto px-3 py-4 space-y-6">
        {navSections.map((section) => (
          <div key={section.title}>
            <p className="text-xs uppercase text-slate-500 px-3 mb-2">
              {section.title}
            </p>
            {section.links.map((link) => (
              <NavItem key={link.to} {...link} />
            ))}
          </div>
        ))}

        {isAdmin && (
          <div>
            <p className="text-xs uppercase text-slate-500 px-3 mb-2">
              Administration
            </p>
            {adminLinks.map((link) => (
              <NavItem key={link.to} {...link} />
            ))}
          </div>
        )}
      </nav>

      <button
        onClick={logout}
        className="flex items-center gap-3 px-6 py-4 border-t border-slate-800 text-sm hover:bg-slate-800"
      >
        <LogOut className="w-4 h-4" />
        Logout
      </button>
    </aside>
  );
}
