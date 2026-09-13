export default function ProgressBar({ percent }) {
    return (
        <div className="w-full bg-slate-100 rounded-full h-2">
            <div
                className="bg-blue-600 h-2 rounded-full transition-all"
                style={{ width: `${percent}%` }}
            />
        </div>
    );
}