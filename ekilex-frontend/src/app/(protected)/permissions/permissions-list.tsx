import { CookieKeys } from "@/lib/enums/cookie-keys.enum";
import { Paths } from "@/lib/enums/paths.enum";
import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import { getPermissions, searchPermissions } from "./actions";
import PermissionToggle from "./permission-toggle";
import AddDataset from "./add-dataset";
import { UserPermissionType } from "@/lib/models/permissions/permissions";
import { Dataset } from "./dataset";
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
  const day = date.getDate().toString().padStart(2, '0');
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  return `${day}.${month}.${date.getFullYear()}`;
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
            <tr className="even:bg-eki-gray-100 align-top" key={permission.id}>
              <td className="border-t border-eki-gray-200">
                {getDateReadableFormat(permission.createdOn)}
              </td>
              <td className="border-t border-eki-gray-200">
                {permission.name}
              </td>
              <td className="border-t border-eki-gray-200">
                {permission.email}
              </td>
              <td className="border-t border-eki-gray-200">
                {permission.apiKeyExists ? "+" : "-"}
              </td>
              <td className="border-t border-eki-gray-200">
                <PermissionToggle
                  isDisabled={
                    userPermissions.userRoleData.userRole.userId ===
                      permission.id || !userPermissions.userRoleData.admin
                  }
                  defaultValue={permission.apiCrud}
                  type={UserPermissionType.CRUD}
                  targetUserId={permission.id}
                  title="Crud"
                />
              </td>
              <td className="border-t border-eki-gray-200">
                <PermissionToggle
                  isDisabled={
                    userPermissions.userRoleData.userRole.userId ===
                      permission.id || !userPermissions.userRoleData.admin
                  }
                  defaultValue={permission.admin}
                  type={UserPermissionType.ADMIN}
                  targetUserId={permission.id}
                  title="Admin"
                />
              </td>
              <td className="border-t border-eki-gray-200">
                <PermissionToggle
                  isDisabled={
                    userPermissions.userRoleData.userRole.userId ===
                      permission.id || !userPermissions.userRoleData.admin
                  }
                  defaultValue={permission.master}
                  type={UserPermissionType.MASTER}
                  targetUserId={permission.id}
                  title="Ülevaataja"
                />
              </td>
              <td className="border-t border-eki-gray-200">
                <PermissionToggle
                  isDisabled={
                    userPermissions.userRoleData.userRole.userId ===
                      permission.id || !userPermissions.userRoleData.admin
                  }
                  defaultValue={permission.enabled}
                  type={UserPermissionType.ENABLED}
                  targetUserId={permission.id}
                  title="Lubatud"
                />
              </td>
              <td className="border-t border-eki-gray-200">
                {permission.reviewComment}
              </td>
              <td className="border-t border-eki-gray-200">-</td>
              <td className="border-t border-eki-gray-200">
                <div className="flex gap-4 w-full">
                  <AddDataset
                    isDisabled={
                      userPermissions.userRoleData.userRole.userId ===
                        permission.id || !userPermissions.userRoleData.admin
                    }
                    targetUserId={permission.id}
                    datasetOptions={userPermissions.userOwnedDatasets.map(
                      (dataset) => ({
                        label: dataset.name,
                        value: dataset.code,
                      })
                    )}
                    authOptions={userPermissions.authorityOperations.map(
                      (auth) => ({ label: auth, value: auth })
                    )}
                    languageOptions={userPermissions.languages.map(
                      (language) => ({
                        label: language.value,
                        value: language.code,
                      })
                    )}
                  />

                  <ul className="text-sm flex flex-col gap-1">
                    {permission.datasetPermissions.map((datasetPermission) => (
                      <li key={datasetPermission.id}>
                        <Dataset
                          isDisabled={
                            userPermissions.userRoleData.userRole.userId ===
                              datasetPermission.userId ||
                            !userPermissions.userRoleData.admin
                          }
                          permissionId={datasetPermission.id}
                        >
                          {datasetPermission.datasetCode}{" "}
                          {datasetPermission.authOperation}{" "}
                          {datasetPermission.authLangValue ?? ""}
                        </Dataset>
                      </li>
                    ))}
                  </ul>
                </div>
              </td>
            </tr>
          );
        })}
      </tbody>
    </table>
  );
}
