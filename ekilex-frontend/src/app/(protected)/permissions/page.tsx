import { PageProps } from "../../../../.next/types/app/page";
import PermissionsForm from "./permissions-form";
import PermissionsList from "./permissions-list";

export default async function Page({ searchParams }: PageProps) {
  return (
    <section className="shadow-md rounded-sm overflow-hidden">
      <h1 className="bg-gray-50 px-5 pt-1 pb-4 text-gray-600 border-b border-gray-300 border-solid w-full">
        Kasutajaõiguste haldus
      </h1>
      <div className="p-4 flex flex-col gap-4">
        <PermissionsForm searchParams={searchParams} />
        <PermissionsList searchParams={searchParams} />
      </div>
    </section>
  );
}
