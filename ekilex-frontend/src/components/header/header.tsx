import Link from "next/link";

export const Header = () => {
  return (
    <div className="bg-eki-blue h-12 flex items-center px-4 gap-2 text-white">
      <Link href='/' className="text-sm">Ekilex</Link>
      <span className="bg-eki-warning px-12 text-eki-blue font-bold">TEST</span>
    </div>
  );
};
