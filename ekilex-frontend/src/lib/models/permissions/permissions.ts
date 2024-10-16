interface DatasetPermission {
  id: number;
  userId: number;
  datasetCode: string;
  datasetName: string;
  authOperation: AuthorityOperations;
  authItem: "DATASET";
  authLang: string | null;
  authLangValue: string | null;
  superiorDataset: boolean;
}

interface UserPermission {
  id: number;
  name: string;
  email: string;
  apiKeyExists: boolean;
  apiCrud: boolean;
  admin: boolean;
  master: boolean;
  enabled: boolean;
  reviewComment: string | null;
  createdOn: string;
  enablePending: boolean;
  applications: unknown[];
  datasetPermissions: DatasetPermission[];
}

export interface UserFilter {
  userNameFilter: string;
  userPermDatasetCodeFilter: string | null;
  userEnablePendingFilter: string | null;
  orderBy: string;
  ekiUserPermissions: UserPermission[];
}

export interface UserDataset {
  code: string;
  name: string;
  public: boolean;
  visible: boolean;
  superior: boolean;
}

export interface UserRoleData {
  userRole: DatasetPermission;
  admin: boolean;
  master: boolean;
  roleSelected: boolean;
  crudRoleSelected: boolean;
  datasetOwnerOrAdmin: boolean;
  datasetCrudOwnerOrAdmin: boolean;
  roleChangeEnabled: boolean;
  lexemeActiveTagChangeEnabled: boolean;
}

export type AuthorityOperations = "OWN" | "CRUD" | "READ";

export const authorityOperations: AuthorityOperations[] = [
  "OWN",
  "CRUD",
  "READ",
];

export interface Language {
  name: "LANGUAGE";
  code: string;
  value: string;
}

export interface UserPermissionsInit {
  userRoleData: UserRoleData;
  userOwnedDatasets: UserDataset[];
  authorityOperations: AuthorityOperations[];
  languages: Language[];
}

export enum UserPermissionType {
  CRUD = "crud",
  ADMIN = "admin",
  MASTER = "master",
  ENABLED = "enabled",
}
