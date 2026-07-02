export type Messages = Record<string, string>;

export interface EtymNode {
  kind: 'node';
  id: number;
  wordId: number;
  language: string;
  langValue: string;
  year: string;
  value: string;
  variants: string[];
  comments: string[];
  sources: string[];
  notes: string[];
  questionableTooltip: Tooltip | null;
  selfQuestionableTip: Tooltip | null;
  detailTip: string;
}

export interface EtymGroup {
  kind: 'group';
  name: string;
  nodes: EtymNode[];
  questionableTooltip: Tooltip | null;
}

export interface EtymLabel {
  kind: 'label';
  text: string;
}

export interface EtymLevel {
  level: number;
  connector: 'chain' | 'merge';
  alternative: boolean;
  items: Array<EtymNode | EtymGroup | EtymLabel>;
}

export interface EtymTree {
  typeLabel: string;
  levels: EtymLevel[];
}

interface Tooltip {
  html: string;
  text: string;
}

export function escapeText(text: unknown): string {
  const div = document.createElement('div');
  div.textContent = text == null ? '' : String(text);
  return div.innerHTML;
}

export function escapeAttr(text: unknown): string {
  return escapeText(text).replace(/"/g, '&quot;');
}

export function mapTree(data: any, messages: Messages): EtymTree {
  const apiLevels: any[] = (data && data.levels) || [];
  const rows = mergeByLevel(apiLevels);
  const levels: EtymLevel[] = [];

  rows.forEach((row) => {
    const groups: any[] = row.groups;
    const compound = groups.find((group) => group.groupType === 'COMPOUND');
    if (compound) {
      levels.push({
        level: row.level,
        connector: 'merge',
        alternative: false,
        items: (compound.groupMembers || []).map((member: any) => mapNode(member, compound, messages)),
      });
    } else {
      levels.push({
        level: row.level,
        connector: 'chain',
        alternative: groups.some((group) => group.groupType === 'ALTERNATIVE'),
        items: mapLevelItems(groups, messages),
      });
    }
  });
  return { typeLabel: extractTypeLabel(apiLevels), levels };
}

function mergeByLevel(apiLevels: any[]): Array<{ level: number; groups: any[] }> {
  const rows: Array<{ level: number; groups: any[] }> = [];
  apiLevels.forEach((apiLevel) => {
    const last = rows[rows.length - 1];
    if (last && last.level === apiLevel.level) {
      last.groups.push(...(apiLevel.groups || []));
    } else {
      rows.push({ level: apiLevel.level, groups: [...(apiLevel.groups || [])] });
    }
  });
  return rows;
}

function extractTypeLabel(levels: any[]): string {
  const first = levels[0];
  if (!first || !first.groups || !first.groups.length) {
    return '';
  }
  const root = first.groups.find((group: any) => group.groupType === 'ROOT');
  return (root || first.groups[0]).etymologyTypeCode || '';
}

function mapLevelItems(groups: any[], messages: Messages): Array<EtymNode | EtymGroup> {
  const items: Array<EtymNode | EtymGroup> = [];
  groups.forEach((group) => {
    if (group.groupType === 'LANGUAGE_GROUP') {
      items.push({
        kind: 'group',
        name: group.languageGroupName,
        nodes: (group.groupMembers || []).map((member: any) => mapNode(member, undefined, messages)),
        questionableTooltip: questionableTooltip(group, messages),
      });
    } else {
      (group.groupMembers || []).forEach((member: any) => items.push(mapNode(member, group, messages)));
    }
  });
  return items;
}

function questionableNote(messages: Messages, key: string): Tooltip {
  const noteText = messages[key];
  return { html: '<span class="etym-questionable__note">' + escapeText(noteText) + '</span>', text: noteText };
}

function questionableTooltip(group: any, messages: Messages): Tooltip | null {
  if (!group || !group.questionable) {
    return null;
  }
  return questionableNote(messages, 'lex.wordetym.questionable.tooltip');
}

function memberQuestionableTip(member: any, messages: Messages): Tooltip | null {
  if (!member || !member.questionable) {
    return null;
  }
  return questionableNote(messages, 'lex.wordetym.questionable.member');
}

function mapNode(member: any, group: any, messages: Messages): EtymNode {
  const node: EtymNode = {
    kind: 'node',
    id: member.id,
    wordId: member.wordId,
    language: member.lang,
    langValue: member.langValue,
    year: member.etymologyYear,
    value: member.valuePrese,
    variants: (member.variantWords || []).map((v: any) => v.valuePrese).filter(Boolean),
    comments: (member.comments || []).map((c: any) => c.valuePrese).filter(Boolean),
    sources: (member.sourceLinks || []).map((s: any) => s.name || s.sourceName).filter(Boolean),
    notes: (member.notes || []).map((n: any) => n.valuePrese).filter(Boolean),
    questionableTooltip: questionableTooltip(group, messages),
    selfQuestionableTip: memberQuestionableTip(member, messages),
    detailTip: '',
  };

  node.detailTip = nodeTipHtml(node, messages);
  return node;
}

export function renderTree(tree: EtymTree, messages: Messages): string {
  if (!tree.levels.length) {
    return '';
  }
  return '<div class="etym-tree">'
    + renderHeader(tree, messages)
    + '<div class="etym-tree__canvas">'
    + '<svg class="etym-tree__links" aria-hidden="true"></svg>'
    + '<div class="etym-tree__levels">' + tree.levels.map(renderLevel).join('') + '</div>'
    + '</div>'
    + '<div class="etym-tip" role="tooltip"></div>'
    + '</div>';
}

const EYE ='<svg class="etym-tree__icon" viewBox="0 0 24 24" width="16" height="16" fill="currentColor" aria-hidden="true"><path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17a5 5 0 110-10 5 5 0 010 10zm0-8a3 3 0 100 6 3 3 0 000-6z"/></svg>';
const EYE_OFF = '<svg class="etym-tree__icon" viewBox="0 0 24 24" width="16" height="16" fill="currentColor" aria-hidden="true"><path d="M12 7a5 5 0 015 5c0 .65-.13 1.26-.36 1.83l2.92 2.92A11.8 11.8 0 0023 12c-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16A5 5 0 0112 7zM2 4.27l2.28 2.28.46.46A11.8 11.8 0 001 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zm5.53 5.53l1.55 1.55a3 3 0 003.65 3.65l1.55 1.55A5 5 0 017.53 9.8zM12 9a3 3 0 013 3l-3-3z"/></svg>';

export function toggleButtonInner(shown: boolean, messages: Messages): string {
  const icon = shown ? EYE_OFF : EYE;
  const text = shown ? messages['lex.wordetym.comments.hide'] : messages['lex.wordetym.comments.show'];
  return icon + '<span class="etym-tree__toggle-text">' + text + '</span>';
}

function renderHeader(tree: EtymTree, messages: Messages): string {
  const type = tree.typeLabel
    ? '<span class="etym-tree__type">' + escapeText(tree.typeLabel) + '</span>'
    : '';
  return '<div class="etym-tree__header">'
    + '<div class="etym-tree__header-row">'
    + type
    + '<button type="button" class="etym-tree__toggle" data-shown="true">'
    + toggleButtonInner(true, messages)
    + '</button>'
    + '</div>'
    + '<div class="etym-tree__hint">' + (messages['lex.wordetym.detail.hint'] || '') + '</div>'
    + '</div>';
}

function renderLevel(level: EtymLevel, index: number): string {
  const connector = level.connector === 'merge' ? ' data-connector="merge"' : '';
  const topLevel = index === 0;

  return '<div class="etym-tree__level"' + connector + '>'
    + level.items.map((item) => renderItem(item, topLevel)).join('')
    + '</div>';
}

function renderItem(item: EtymNode | EtymGroup | EtymLabel, topLevel: boolean): string {
  if (item.kind === 'group') {
    return renderGroup(item, topLevel);
  }
  if (item.kind === 'label') {
    return '<div class="etym-label">' + escapeText(item.text) + '</div>';
  }
  return renderNode(item, topLevel);
}

function renderGroup(group: EtymGroup, topLevel: boolean): string {
  return '<div class="etym-group">'
    + (topLevel ? renderQuestionableConnector(group.questionableTooltip) : '')
    + '<div class="etym-group__name">' + escapeText(group.name) + '</div>'
    + '<div class="etym-group__nodes">' + group.nodes.map((node) => renderNode(node, false)).join('') + '</div>'
    + '</div>';
}

function renderNode(node: EtymNode, topLevel: boolean): string {
  const hasComments = node.comments.length > 0;
  const variants = node.variants
    .map((variant) => '<span class="etym-node__variant">' + variant + '</span>')
    .join('');
  const head ='<div class="etym-node__head" tabindex="0" role="button" data-tip="' + escapeAttr(node.detailTip) + '">'
    + '<span class="etym-node__lang">' + escapeText(node.language) + '</span>'
    + '<span class="etym-node__value">'
    + '<span class="etym-node__word">' + (node.value || '') + renderQuestionableBadge(node.selfQuestionableTip) + '</span>'
    + variants
    + '</span>'
    + '</div>';
  const comments = hasComments
    ? '<div class="etym-node__comments">'
      + node.comments.map((comment) => '<div class="etym-node__comment">' + comment + '</div>').join('')
      + '</div>'
    : '';
  return '<div class="etym-node' + (hasComments ? '' : ' etym-node--no-comment') + '">'
    + (topLevel ? renderQuestionableConnector(node.questionableTooltip) : '') + head + comments
    + '</div>';
}

function nodeTipHtml(node: EtymNode, messages: Messages): string {
  const rows = [tooltipRow(messages['lex.wordetym.lang'], [escapeText(node.langValue)])];
  if (node.year) {
    rows.push(tooltipRow(messages['lex.wordetym.year'], [escapeText(node.year)]));
  }
  if (node.sources.length) {
    rows.push(tooltipRow(messages['lex.source.link'], node.sources));
  }
  if (node.notes.length) {
    rows.push(tooltipRow(messages['lex.wordetym.notes'], node.notes));
  }
  return rows.join('');
}

function tooltipRow(label: string, items: string[]): string {
  const body = items.map((item) => '<div class="etym-tooltip__item">' + item + '</div>').join('');
  return '<div class="etym-tooltip__row">'
    + '<div class="etym-tooltip__label">' + (label || '') + '</div>'
    + '<div class="etym-tooltip__value">' + body + '</div>'
    + '</div>';
}

function renderQuestionableBadge(tooltip: Tooltip | null): string {
  if (!tooltip) {
    return '';
  }
  return '<span class="etym-questionable etym-questionable--inline" tabindex="0" role="button"'
    + ' aria-label="' + escapeAttr(tooltip.text) + '"'
    + ' data-tip="' + escapeAttr(tooltip.html) + '">?</span>';
}

function renderQuestionableConnector(tooltip: Tooltip | null): string {
  if (!tooltip) {
    return '';
  }

  return '<div class="etym-questionable-connector">'
    + '<span class="etym-questionable" tabindex="0" role="button"'
    + ' aria-label="' + escapeAttr(tooltip.text) + '"'
    + ' data-tip="' + escapeAttr(tooltip.html) + '">?</span>'
    + '<span class="etym-questionable-connector__line" aria-hidden="true"></span>'
    + '</div>';
}

const LINK_ICON_PATH = 'M4.425 10.5C4.425 9.2175 5.4675 8.175 6.75 8.175H9.75V6.75H6.75C4.68 6.75 3 8.43 3 10.5 3 12.57 4.68 14.25 6.75 14.25H9.75V12.825H6.75C5.4675 12.825 4.425 11.7825 4.425 10.5ZM7.5 11.25H13.5V9.75H7.5V11.25ZM14.25 6.75H11.25V8.175H14.25C15.5325 8.175 16.575 9.2175 16.575 10.5 16.575 11.7825 15.5325 12.825 14.25 12.825H11.25V14.25H14.25C16.32 14.25 18 12.57 18 10.5 18 8.43 16.32 6.75 14.25 6.75Z';
const MERGE_ICON_PATH = 'M17 20.41 18.41 19 15 15.59 13.59 17 17 20.41ZM7.5 8H11v5.59L5.59 19 7 20.41l6-6V8h3.5L12 3.5 7.5 8Z';

export function avg(values: number[]): number {
  return values.reduce((sum, value) => sum + value, 0) / values.length;
}

export function curvePath(x1: number, y1: number, x2: number, y2: number, questionable = false): string {
  const midY = (y1 + y2) / 2;
  const cls = questionable ? 'etym-tree__line etym-tree__line--questionable' : 'etym-tree__line';
  return '<path class="' + cls + '" d="M ' + x1 + ' ' + y1
    + ' C ' + x1 + ' ' + midY + ', ' + x2 + ' ' + midY + ', ' + x2 + ' ' + y2 + '" />';
}

export function junction(x: number, y: number, type: 'chain' | 'merge', tipHtml: string): string {
  const merge = type === 'merge';
  const path = merge ? MERGE_ICON_PATH : LINK_ICON_PATH;
  const iconTransform = merge ? 'translate(-8,-8) scale(0.667)' : 'translate(-9.45,-9.45) scale(0.9)';
  const interactive = tipHtml
    ? ' etym-tree__junction--interactive" tabindex="0" role="button" data-tip="' + escapeAttr(tipHtml) + '"'
    : '"';
  return '<g class="etym-tree__junction' + interactive + ' transform="translate(' + x + ',' + y + ')">'
    + '<circle r="12" />'
    + '<path class="etym-tree__junction-icon" transform="' + iconTransform + '" d="' + path + '" />'
    + '</g>';
}

export function questionJunction(x: number, y: number, tooltip: Tooltip): string {
  return '<g class="etym-tree__junction etym-tree__junction--interactive etym-tree__junction--questionable"'
    + ' tabindex="0" role="button"'
    + ' aria-label="' + escapeAttr(tooltip.text) + '"'
    + ' data-tip="' + escapeAttr(tooltip.html) + '"'
    + ' transform="translate(' + x + ',' + y + ')">'
    + '<circle r="12" />'
    + '<text class="etym-tree__junction-question" text-anchor="middle" dominant-baseline="central">?</text>'
    + '</g>';
}

export function levelQuestionableTip(level: EtymLevel | undefined): Tooltip | null {
  if (!level) {
    return null;
  }
  for (const item of level.items) {
    if ((item.kind === 'node' || item.kind === 'group') && item.questionableTooltip) {
      return item.questionableTooltip;
    }
  }
  return null;
}

export function firstNodeOf(items: Array<EtymNode | EtymGroup | EtymLabel>): EtymNode | null {
  for (let i = 0; i < items.length; i++) {
    const item = items[i];
    if (item.kind === 'node') {
      return item;
    }
    if (item.kind === 'group' && item.nodes && item.nodes.length) {
      return item.nodes[0];
    }
  }
  return null;
}

