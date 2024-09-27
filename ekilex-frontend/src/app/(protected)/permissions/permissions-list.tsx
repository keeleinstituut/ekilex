import { searchPermissions } from "@/lib/actions/search-permissions";
const headers = [
  "Kuupäev",
  "Nimi",
  "E-post",
  "Api",
  "Crud",
  "Admin",
  "Ülevaataja",
  "Lubatud",
  "Kommentaar",
  "Avaldused",
  "Load",
];

const getDateReadableFormat = (isoString: string) => {
  const date = new Date(isoString);
  return `${date.getDate()}.${date.getMonth() + 1}.${date.getFullYear()}`;
};

export default async function PermissionsList({
  searchParams,
}: {
  searchParams: {
    [key: string]: string;
  };
}) {
  const permissions = await searchPermissions(searchParams);
  return (
    <table className="w-full table-auto">
      <thead>
        <tr className="text-left">
          {headers.map((header) => (
            <th key={header}>{header}</th>
          ))}
        </tr>
      </thead>
      <tbody>
        {permissions?.ekiUserPermissions?.map((permission) => {
          return (
            <tr key={permission.id}>
              <td>{getDateReadableFormat(permission.createdOn)}</td>
              <td>{permission.name}</td>
              <td>{permission.email}</td>
              <td>{permission.apiKeyExists ? "+" : "-"}</td>
              <td>{permission.apiCrud ? "+" : "-"}</td>
              <td>{permission.admin ? "+" : "-"}</td>
              <td>{permission.master ? "+" : "-"}</td>
              <td>{permission.enabled ? "+" : "-"}</td>
              <td>{permission.reviewComment}</td>
              <td>-</td>
              <td>-</td>
            </tr>
          );
        })}
      </tbody>
    </table>
  );
}
