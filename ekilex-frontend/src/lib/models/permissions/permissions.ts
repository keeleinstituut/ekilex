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
  datasetPermissions: unknown[];
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
  userRole: {
    id: number;
    userId: number;
    datasetCode: string;
    datasetName: string;
    authOperation: string;
    authItem: string;
    authLang: null | string;
    authLangValue: null | string;
    superiorDataset: boolean;
  };
  admin: boolean;
  master: boolean;
  roleSelected: boolean;
  crudRoleSelected: boolean;
  datasetOwnerOrAdmin: boolean;
  datasetCrudOwnerOrAdmin: boolean;
  roleChangeEnabled: boolean;
  lexemeActiveTagChangeEnabled: boolean;
}

export interface UserPermissionsInit {
  userRoleData: UserRoleData;
  userOwnedDatasets: UserDataset[];
}

export type UserPermissionType = "crud" | "admin" | "master" | "enabled";
