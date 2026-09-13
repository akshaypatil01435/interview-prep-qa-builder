export default function Button({
                                   children,
                                   variant = "primary",
                                   className = "",
                                   ...props
                               }) {
    const base =
        "px-4 py-2 rounded-lg font-medium text-sm transition-colors";

    const variants = {
        primary: "bg-blue-600 text-white hover:bg-blue-700",
        secondary:
            "bg-white border border-slate-200 text-slate-700 hover:bg-slate-50",
        danger: "bg-red-600 text-white hover:bg-red-700",
    };

    const disabledStyles = props.disabled ? "opacity-50 cursor-not-allowed" : "";

    return (
        <button
            className={`${base} ${variants[variant]} ${disabledStyles} ${className}`}
            {...props}
        >
            {children}
        </button>
    );
}