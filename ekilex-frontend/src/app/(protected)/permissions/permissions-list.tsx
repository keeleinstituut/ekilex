import { CookieKeys } from "@/lib/enums/cookie-keys.enum";
import { Paths } from "@/lib/enums/paths.enum";
import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import { getPermissions, searchPermissions } from "./actions";
import PermissionToggle from "./permission-toggle";
import AddDataset from "./add-data-set";
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
  const sessionId = cookies().get(CookieKeys.JSESSIONID);

  if (!sessionId?.value) {
    redirect(Paths.LOGIN);
  }

  const list = await searchPermissions(searchParams, sessionId.value);
  const userPermissions = await getPermissions(sessionId.value);

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
        {list?.ekiUserPermissions?.map((permission) => {
          return (
            <tr key={permission.id}>
              <td>{getDateReadableFormat(permission.createdOn)}</td>
              <td>{permission.name}</td>
              <td>{permission.email}</td>
              <td>{permission.apiKeyExists ? "+" : "-"}</td>
              <td>
                <PermissionToggle
                  isDisabled={
                    userPermissions.userRoleData.userRole.userId ===
                      permission.id || !userPermissions.userRoleData.admin
                  }
                  defaultValue={permission.apiCrud}
                  type="crud"
                  targetUserId={permission.id}
                  title="Crud"
                />
              </td>
              <td>
                <PermissionToggle
                  isDisabled={
                    userPermissions.userRoleData.userRole.userId ===
                      permission.id || !userPermissions.userRoleData.admin
                  }
                  defaultValue={permission.admin}
                  type="admin"
                  targetUserId={permission.id}
                  title="Admin"
                />
              </td>
              <td>
                <PermissionToggle
                  isDisabled={
                    userPermissions.userRoleData.userRole.userId ===
                      permission.id || !userPermissions.userRoleData.admin
                  }
                  defaultValue={permission.master}
                  type="master"
                  targetUserId={permission.id}
                  title="Ülevaataja"
                />
              </td>
              <td>
                <PermissionToggle
                  isDisabled={
                    userPermissions.userRoleData.userRole.userId ===
                      permission.id || !userPermissions.userRoleData.admin
                  }
                  defaultValue={permission.enabled}
                  type="enabled"
                  targetUserId={permission.id}
                  title="Lubatud"
                />
              </td>
              <td>{permission.reviewComment}</td>
              <td>-</td>
              <td>
                <AddDataset
                  isDisabled={
                    userPermissions.userRoleData.userRole.userId ===
                      permission.id || !userPermissions.userRoleData.admin
                  }
                  targetUserId={permission.id}
                />
              </td>
            </tr>
          );
        })}
      </tbody>
    </table>
  );
}
