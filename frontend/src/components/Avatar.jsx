export default function Avatar({ name, size = "md" }) {
    const initials = name
        ?.split(" ")
        .map((w) => w[0])
        .join("")
        .slice(0, 2)
        .toUpperCase();

    const sizes = {
        sm: "w-8 h-8 text-xs",
        md: "w-10 h-10 text-sm",
        lg: "w-14 h-14 text-lg",
    };

    return (
        <div
            className={`${sizes[size]} rounded-full bg-slate-800 text-white flex items-center justify-center font-medium shadow-sm`}
        >
            {initials || "?"}
        </div>
    );
}